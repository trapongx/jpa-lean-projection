package com.runninglane.jpa.projection.mapper.embedded

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.reflection.annotatedWith
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure

internal class EmbeddedPropertyMapper(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper?,
    entityClass: KClass<*>,
    projectionClass: KClass<*>,
    projectionClassImpl: KClass<*>,
    override val propertyName: String,
    hadJoinFetch: Boolean,
) : Mapper, PropertyMapper {

    private val srcProp: KProperty1<out Any, *> = entityClass.memberProperties
        .find { it.name == propertyName }
        ?.also {
            require(!it.annotatedWith<javax.persistence.EmbeddedId>()) {
                "ID property must not be projected to different type. Property: $propertyName, Entity: ${entityClass.qualifiedName}, Projection: ${projectionClassImpl.qualifiedName}"
            }
        }
        ?: error("Projected property `$propertyName` from projection class ${projectionClassImpl.qualifiedName} is not found in entity class `${entityClass.qualifiedName}`")

    private val srcPropType: KClass<*> = srcProp.returnType.jvmErasure

    private val isSrcPropNullable: Boolean = srcProp.returnType.isMarkedNullable || run {
        // If it is Java class and type is not primitive, then srcProp is nullable
        val isKotlinClass = entityClass.annotations.any { it is Metadata }
        !isKotlinClass && srcProp.javaField?.type?.isPrimitive != true
    }

    private val prop: KProperty1<out Any, *> = projectionClass.memberProperties
        .first { it.name == propertyName }

    private val propType: KClass<*> = prop.returnType.jvmErasure

    private val propTypeImpl = projectorFactory.projectionFactory.getImplementation(srcPropType, propType)

    private val mapper: EmbeddableClassMapper = EmbeddableClassMapper(projectorFactory, this, srcPropType, propType, propTypeImpl, hadJoinFetch)

    private val propertyAccessor: PropertyAccessor = PropertyAccessor.of(projectorFactory.projectionFactory, entityClass, projectionClassImpl, propertyName)

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = listOf(mapper)

    override fun hasJoinFetch(): Boolean = false

    override fun buildSelections(path: Path<*>, tupleIndexCounter: TupleIndexCounter): List<Expression<*>> {
        val embeddedPath = path.get<Any>(propertyName)
        return mapper.buildSelections(embeddedPath, tupleIndexCounter)
    }

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        val (instance, result) = if (isSrcPropNullable && isAllTupleElementsNull(tuple)) {
            null to (emptyList<Fetcher>() to null)
        } else {
            val instance = projectorFactory.projectionFactory.create(srcPropType, propType)
            val result = mapper.readTuple(tuple, instance, projection, projectionIdentityMap)
            instance to result
        }
        propertyAccessor.set(projection, instance)
        return result
    }
}