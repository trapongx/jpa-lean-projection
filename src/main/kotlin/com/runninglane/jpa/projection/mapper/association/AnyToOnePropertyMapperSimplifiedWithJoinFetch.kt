package com.runninglane.jpa.projection.mapper.association

import com.runninglane.jpa.projection.reflection.annotatedWith
import com.runninglane.jpa.projection.reflection.getAnnotation
import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionFactory
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.mapper.sametype.SameTypePropertyMapper
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.From
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Path
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

internal class AnyToOnePropertyMapperSimplifiedWithJoinFetch(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    private val propertyName: String,
    hadJoinFetch: Boolean
) : Mapper, ProbablyInvertible {

    private val srcProp: KProperty1<out Any, *> = entityClass.memberProperties.firstOrNull { it.name == propertyName }
        ?: error("Projected property `$propertyName` not found in class `${entityClass.qualifiedName}`")

    private val srcPropType: KClass<*> = srcProp.returnType.jvmErasure

    private val prop: KProperty1<out Any, *> = projectionClassImpl.memberProperties
        .first { it.name == propertyName }

    private val propType: KClass<*> = prop.returnType.jvmErasure
        .let { projectorFactory.projectionFactory.getImplementation(srcPropType, it) }

    private val mapper = EntityClassMapper.of(
        projectorFactory,
        this,
        srcPropType,
        propType,
        hadJoinFetch,
        null
    )

    private val idMapper = mapper.getChildren().filterIsInstance<SameTypePropertyMapper>().first { it.isIdProperty }

    private val propertyAccessor = PropertyAccessor.of(projectorFactory.projectionFactory, entityClass, projectionClassImpl, propertyName)

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = listOfNotNull(mapper)

    override fun hasJoinFetch(): Boolean = mapper.hasJoinFetch()

    override fun buildSelections(path: Path<*>, tupleIndexCounter: TupleIndexCounter): List<Expression<*>> {
        val associationPath = when (path) {
            is From<*, *> -> path.join<Any, Any>(propertyName, JoinType.LEFT)
            else -> path.get(propertyName)
        }
        return mapper.buildSelections(associationPath, tupleIndexCounter)
    }

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        val id = tuple[idMapper.tupleIndex]
        val (instance, result) = when (id) {
            null -> null to (emptyList<Fetcher>() to null)

            else -> {
                val reusableInstance = projectionIdentityMap.get(srcPropType, propType, id)

                when (reusableInstance) {
                    null -> {
                        val newInstance = projectorFactory.projectionFactory.create(srcPropType, propType).also {
                            projectionIdentityMap.add(srcPropType, propType, id, it)
                        }
                        val result = mapper.readTuple(tuple, newInstance, projection, projectionIdentityMap)
                        newInstance to result
                    }

                    else -> reusableInstance to (emptyList<Fetcher>() to null)
                }
            }
        }
        propertyAccessor.set(projection, instance)
        return result
    }

    override fun checkAssociationInvertibility(
        entityClassOnRightSide: KClass<*>,
        projectionClassOnRightSide: KClass<*>,
        propertyName: String
    ): ProbablyInvertible.AssociationInvertibilityCheckResult {
        val otherSrcProp: KProperty1<out Any, *> = entityClassOnRightSide.memberProperties
            .first { it.name == propertyName }

        val otherSrcPropType = otherSrcProp.returnType.jvmErasure

        val otherPropType: KClass<*> = projectionClassOnRightSide.memberProperties
            .first { it.name == propertyName }
            .returnType.jvmErasure

        val checkMappedBy by lazy {
            when {
                srcProp.annotatedWith<OneToOne>() -> {
                    otherSrcProp.getAnnotation<OneToOne>()?.mappedBy == this.propertyName
                            || srcProp.getAnnotation<OneToOne>()?.mappedBy == propertyName
                }

                else -> otherSrcProp.getAnnotation<OneToMany>()?.mappedBy == this.propertyName
            }
        }

        val isInversion = otherSrcPropType == entityClass
                && entityClassOnRightSide == srcPropType
                && checkMappedBy

        val isProjectionTypeCompatible = isInversion
                && projectionClassOnRightSide.isSubclassOf(propType)
                && projectionClass.isSubclassOf(otherPropType)

        return ProbablyInvertible.AssociationInvertibilityCheckResult(isInversion, isProjectionTypeCompatible)
    }

}