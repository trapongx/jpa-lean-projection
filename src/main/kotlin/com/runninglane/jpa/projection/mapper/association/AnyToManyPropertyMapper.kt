package com.runninglane.jpa.projection.mapper.association

import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import javax.persistence.criteria.From
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Path
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

internal class AnyToManyPropertyMapper(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    override val propertyName: String,
    hadJoinFetch: Boolean
) : SimplifiableMapper, PropertyMapper {

    init {
        assert(!projectionClassImpl.isAbstract)
    }

    private val srcProp: KProperty1<out Any, *> = entityClass.memberProperties
        .find { it.name == propertyName }
        ?: error("Projected property `$propertyName` not found in class `${entityClass.qualifiedName}`")

    private val srcPropType: KClass<*> = srcProp.returnType.jvmErasure

    private val srcPropTypeArgType: KClass<*> = srcProp.returnType.arguments.first().type!!.jvmErasure

    private val prop: KProperty1<out Any, *> = projectionClassImpl
        .memberProperties
        .first { it.name == propertyName }

    private val propType: KClass<*> = prop.returnType.jvmErasure

    private val propTypeArgType: KClass<*> = prop.returnType.arguments.first().type!!.jvmErasure
        .let { projectorFactory.projectionFactory.getImplementation(srcPropTypeArgType, it) }

    private val isCollection: Boolean = srcPropType == Collection::class
    private val isList: Boolean = srcPropType.isSubclassOf(List::class)
    private val isSet: Boolean = srcPropType.isSubclassOf(Set::class)

    init {
        require(isCollection || isList || isSet) { "Unsupported collection type $propType" }
    }

    private val isMutable: Boolean = when {
        isCollection || isList -> srcPropType.isSubclassOf(MutableList::class)
        isSet -> srcPropType.isSubclassOf(MutableSet::class)
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
        isMap = false,
        isMutable = isMutable,
        propTypeArgType1 = propTypeArgType,
        srcPropTypeArgType1 = srcPropTypeArgType,
    )

    /**
     * Use join fetch if there's no prior join fetch and there's no EntityClassMapper for srcPropTypeArgType in the parent chain.
     */
    val useJoinFetch: Boolean = !hadJoinFetch && run {
        generateSequence<Mapper>(parent) { it.getParent() }.none {
            it is EntityClassMapper && it.entityClass == srcPropTypeArgType
        }
    }

    override fun simplify(): Mapper {
        return if (useJoinFetch) {
            val associationPathBuilder: (Path<*>) -> Path<*> = when {
                isCollection || isList || isSet -> { path ->
                    (path as From<*, *>).join<Any, Any>(propertyName, JoinType.LEFT)
                }
                else -> error("Unsupported collection type $propType")
            }

            AnyToManyPropertyMapperSimplifiedWithJoinFetch(
                projectorFactory,
                parent,
                entityClass,
                projectionClass,
                propertyInfo,
                srcPropTypeArgType,
                propTypeArgType,
                associationPathBuilder
            )
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