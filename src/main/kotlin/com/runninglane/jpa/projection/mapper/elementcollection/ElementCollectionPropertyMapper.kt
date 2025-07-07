package com.runninglane.jpa.projection.mapper.elementcollection

import com.runninglane.jpa.projection.reflection.getEmbeddableClass
import com.runninglane.jpa.projection.reflection.getEntityClass
import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionFactory
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.mapper.map.MapPropertyMapper
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path
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
    hadJoinFetch: Boolean
) : Mapper, PropertyMapper {

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

    private val mapper: Mapper by lazy {
        if (isMap) {
            MapPropertyMapper(projectorFactory, this, entityClass, projectionClass, projectionClassImpl, propertyInfo, hadJoinFetch).simplify()
        } else if (srcPropTypeArgType1.getEntityClass() != null) {
            EntityElementCollectionPropertyMapper(projectorFactory, this, entityClass, projectionClass, projectionClassImpl, propertyInfo, true)
        } else if (srcPropTypeArgType1.getEmbeddableClass() != null) {
            EmbeddableElementCollectionPropertyMapper(projectorFactory, this, entityClass, projectionClass, projectionClassImpl, propertyInfo, true)
        } else {
            SimpleElementCollectionPropertyMapper(projectorFactory, this, entityClass, projectionClass, projectionClassImpl, propertyInfo, true)
        }
    }

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = listOf(mapper)

    override fun hasJoinFetch(): Boolean = mapper.hasJoinFetch()

    override fun buildSelections(
        path: Path<*>,
        tupleIndexCounter: TupleIndexCounter
    ): List<Expression<*>> = mapper.buildSelections(path, tupleIndexCounter)

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> =
        mapper.readTuple(tuple, projection, parentProjection, projectionIdentityMap)
}