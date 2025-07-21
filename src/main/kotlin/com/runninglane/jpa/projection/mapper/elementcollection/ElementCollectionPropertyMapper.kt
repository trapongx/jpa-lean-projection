package com.runninglane.jpa.projection.mapper.elementcollection

import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.mapper.map.MapPropertyMapper
import com.runninglane.jpa.projection.reflection.getEmbeddableClass
import com.runninglane.jpa.projection.reflection.getEntityClass
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

internal class ElementCollectionPropertyMapper(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    override val propertyName: String,
    private val hadJoinFetch: Boolean
) : SimplifiableMapper, PropertyMapper {

    init {
        assert(!projectionClassImpl.isAbstract)
    }

    private val srcProp: KProperty1<out Any, *> = entityClass.memberProperties
        .find { it.name == propertyName }
        ?: error("Projected property `$propertyName` not found in class `${entityClass.qualifiedName}`")

    private val srcPropType: KClass<*> = srcProp.returnType.jvmErasure

    private val srcPropTypeArgType1: KClass<*> = srcProp.returnType.arguments.first().type!!.jvmErasure
    private val srcPropTypeArgType2: KClass<*> by lazy { srcProp.returnType.arguments.last().type!!.jvmErasure }

    private val prop: KProperty1<out Any, *> = projectionClass.memberProperties
        .first { it.name == propertyName }

    private val propType: KClass<*> = prop.returnType.jvmErasure

    private val propTypeArgType1: KClass<*> = prop.returnType.arguments.first().type!!.jvmErasure
        .let { if (it.isAbstract) projectorFactory.projectionFactory.getImplementation(srcPropTypeArgType1, it) else it }

    private val propTypeArgType2: KClass<*> by lazy {
        prop.returnType.arguments.last().type!!.jvmErasure
            .let { if (it.isAbstract) projectorFactory.projectionFactory.getImplementation(srcPropTypeArgType2, it) else it }
    }

    private val isCollection: Boolean = srcPropType == Collection::class
    private val isList: Boolean = srcPropType.isSubclassOf(List::class)
    private val isSet: Boolean = srcPropType.isSubclassOf(Set::class)
    private val isMap: Boolean = srcPropType.isSubclassOf(Map::class)

    init {
        require(isCollection || isList || isSet || isMap) { "Unsupported collection type $propType" }
    }

    private val isMutable: Boolean = when {
        isCollection || isList -> srcPropType.isSubclassOf(MutableList::class)
        isSet -> srcPropType.isSubclassOf(MutableSet::class)
        isMap -> srcPropType.isSubclassOf(MutableMap::class)
        else -> false
    }

    private val propertyInfo: PropertyInfo = PropertyInfo(
        prop,
        propertyName,
        propType,
        accessor = PropertyAccessor.of(projectorFactory.projectionFactory, entityClass, projectionClassImpl, propertyName),
        isCollection = isCollection,
        isList = isList,
        isSet = isSet,
        isMap = isMap,
        isMutable = isMutable,
        propTypeArgType1 = propTypeArgType1,
        propTypeArgType2 = propTypeArgType2,
        srcPropTypeArgType1 = srcPropTypeArgType1,
        srcPropTypeArgType2 = srcPropTypeArgType2
    )

    /**
     * Use join fetch if there's no prior join fetch and there's no EntityClassMapper for srcPropTypeArgType in the parent chain.
     */
    val useJoinFetch: Boolean = !hadJoinFetch && run {
        generateSequence(parent) { it.getParent() }.none {
            it is EntityClassMapper && (it.entityClass == srcPropTypeArgType1 || it.entityClass == srcPropTypeArgType2)
        }
    }

    override fun simplify(): Mapper {
        return if (useJoinFetch) {
            if (isMap) {
                MapPropertyMapper(projectorFactory, parent, entityClass, projectionClass, projectionClassImpl, propertyInfo, hadJoinFetch).simplify()
            } else if (srcPropTypeArgType1.getEntityClass() != null) {
                EntityElementCollectionPropertyMapperUsingJoinFetch(projectorFactory, parent, propertyInfo)
            } else if (srcPropTypeArgType1.getEmbeddableClass() != null) {
                EmbeddableElementCollectionPropertyMapperUsingJoinFetch(projectorFactory, parent, propertyInfo)
            } else {
                SimpleElementCollectionPropertyMapperUsingJoinFetch( parent, propertyInfo)
            }
        } else {
            EntityPropertyMapperUsingFetcher(
                projectorFactory,
                parent,
                entityClass,
                projectionClass,
                projectionClassImpl,
                propertyInfo
            )
        }
    }
}