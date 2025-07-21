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

internal class AnyToOnePropertyMapper(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    override val propertyName: String,
    private val hadJoinFetch: Boolean
) : SimplifiableMapper, PropertyMapper, ProbablyInvertible {

    private val srcProp: KProperty1<out Any, *> = entityClass.memberProperties.firstOrNull { it.name == propertyName }
        ?: error("Projected property `$propertyName` not found in class `${entityClass.qualifiedName}`")

    private val srcPropType: KClass<*> = srcProp.returnType.jvmErasure

    private val prop: KProperty1<out Any, *> = projectionClassImpl.memberProperties
        .first { it.name == propertyName }

    private val propType: KClass<*> = prop.returnType.jvmErasure
        .let { projectorFactory.projectionFactory.getImplementation(srcPropType, it) }

    private val isInversion: Boolean = run {
        generateSequence(parent) { it.getParent() }
            .firstNotNullOfOrNull { it as? ProbablyInvertible }
            ?.let { probablyInvertible ->
                val closestParentEntityClassMapper = generateSequence(parent) { it.getParent() }
                    .firstNotNullOf { it as? EntityClassMapper }
                val propertyNamePath = generateSequence(this as Mapper) { it.getParent() }
                    .takeWhile { it !== closestParentEntityClassMapper }
                    .plus(closestParentEntityClassMapper)
                    .filterIsInstance<PropertyMapper>()
                    .map { it.propertyName }
                    .toList()
                    .reversed()
                    .joinToString(".")
                probablyInvertible.checkAssociationInvertibility(
                    closestParentEntityClassMapper.entityClass,
                    closestParentEntityClassMapper.projectionClassImpl,
                    propertyNamePath
                )
            }
            ?.also { checkResult ->
                if (checkResult.isInvertible) {
                    require(checkResult.isProjectionTypeCompatible) {
                        "Projection of bidirectional association with imbalanced projection type is discouraged at $projectionClassImpl.$propertyName"
                    }
                }
            }?.isInvertible == true
    }

    private val mapper: Mapper? = when {
        isInversion -> null
        else -> EntityClassMapper.of(
            projectorFactory, this, srcPropType, propType, hadJoinFetch, null
        )
    }

    private val propertyAccessor: PropertyAccessor = PropertyAccessor.of(
        projectorFactory.projectionFactory, entityClass, projectionClassImpl, propertyName
    )

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = listOfNotNull(mapper)

    override fun hasJoinFetch(): Boolean = mapper?.hasJoinFetch() == true

    override fun buildSelections(path: Path<*>, tupleIndexCounter: TupleIndexCounter): List<Expression<*>> {
        return when {
            isInversion -> throw SimplifiableMapper.exceptionUsingUnsimplifiedMapper
            else -> {
                val associationPath = (path as From<*, *>).join<Any, Any>(propertyName, JoinType.LEFT)
                mapper!!.buildSelections(associationPath, tupleIndexCounter)
            }
        }
    }

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        return when {
            isInversion -> throw SimplifiableMapper.exceptionUsingUnsimplifiedMapper
            else -> {
                val idMapper =
                    mapper!!.getChildren().filterIsInstance<SameTypePropertyMapper>().first { it.isIdProperty }
                val (instance, result) = if (tuple[idMapper.tupleIndex] == null) {
                    null to (emptyList<Fetcher>() to null)
                } else {
                    val id = tuple[idMapper.tupleIndex]
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
                propertyAccessor.set(projection, instance)
                result
            }
        }
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

    override fun simplify(): Mapper = when {
        isInversion -> AnyToOnePropertyMapperSimplifiedAsBackReference(
            parent,
            propertyName,
            PropertyAccessor.of(projectorFactory.projectionFactory, entityClass, projectionClassImpl, propertyName)
        )
        else -> {
            if (mapper is EntityClassMapper)
                AnyToOnePropertyMapperSimplifiedWithJoinFetch(projectorFactory, parent, entityClass, projectionClass, projectionClassImpl, propertyName, hadJoinFetch)
            else
                mapper!!
        }
    }
}