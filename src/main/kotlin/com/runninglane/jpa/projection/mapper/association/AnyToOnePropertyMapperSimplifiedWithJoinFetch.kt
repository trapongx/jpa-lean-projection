package com.runninglane.jpa.projection.mapper.association

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.mapper.sametype.SameTypePropertyMapper
import com.runninglane.jpa.projection.reflection.annotatedWith
import com.runninglane.jpa.projection.reflection.getAnnotation
import com.runninglane.jpa.projection.reflection.getPropertyAtPath
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

    init {
        assert(
            projectionClass != projectionClassImpl
                    && !projectionClassImpl.isAbstract
                    && projectionClassImpl.isSubclassOf(projectionClass)
        )
    }

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

    private val propTypeImpl = mapper.projectionClassImpl

    private val idMappers = mapper.getChildren().filterIsInstance<SameTypePropertyMapper>().filter { it.isIdProperty }

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
        val id = when (idMappers.size) {
            1 -> tuple[idMappers.first().tupleIndex]
            else -> idMappers.associate { it.propertyName to tuple[it.tupleIndex] }
        }
        val (instance, result) = when (id) {
            null -> null to (emptyList<Fetcher>() to null)

            else -> {
                val reusableInstance = projectionIdentityMap.get(srcPropType, propTypeImpl, id)

                when (reusableInstance) {
                    null -> {
                        val newInstance = projectorFactory.projectionFactory.create(srcPropType, propType).also {
                            projectionIdentityMap.add(srcPropType, propTypeImpl, id, it)
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
        propertyPath: String
    ): ProbablyInvertible.AssociationInvertibilityCheckResult {
        val otherSrcProp = entityClassOnRightSide.getPropertyAtPath(propertyPath)
        val otherSrcPropType = otherSrcProp.returnType.jvmErasure
        val otherPropType: KClass<*> = projectionClassOnRightSide.getPropertyAtPath(propertyPath).returnType.jvmErasure

        val checkMappedBy by lazy {
            when {
                srcProp.annotatedWith<OneToOne>() -> {
                    otherSrcProp?.getAnnotation<OneToOne>()?.mappedBy == this.propertyName
                            || srcProp.getAnnotation<OneToOne>()?.mappedBy == propertyPath
                }

                else -> otherSrcProp?.getAnnotation<OneToMany>()?.mappedBy == this.propertyName
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