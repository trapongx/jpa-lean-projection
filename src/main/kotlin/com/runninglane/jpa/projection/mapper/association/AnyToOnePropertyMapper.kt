package com.runninglane.jpa.projection.mapper.association

import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.mapper.assert.ProjectionClassAssertion
import kotlin.reflect.KClass

internal class AnyToOnePropertyMapper(
    projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val entityClass: KClass<*>,
    projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    override val propertyName: String,
    hadJoinFetch: Boolean
) : SimplifiableMapper, PropertyMapper {

    init {
        assert(ProjectionClassAssertion.isCorrectSemantics(entityClass, projectionClass, projectionClassImpl))
    }

    private val isInversion: Boolean = run {
        generateSequence(parent) { it.getParent() }
            .firstNotNullOfOrNull { it as? ProbablyInvertible }
            ?.let { probablyInvertible ->
                val closestParentEntityClassMapper = generateSequence(parent) { it.getParent() }
                    .firstNotNullOf { it as? EntityClassMapper }

                val propertyNamePath = generateSequence(this as Mapper) {
                    if (it == this) parent else it.getParent()
                }
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

    private val propertyAccessor: PropertyAccessor = PropertyAccessor.of(
        projectorFactory.projectionFactory, entityClass, projectionClassImpl, propertyName
    )

    private val mapper: Mapper = when {
        isInversion -> AnyToOnePropertyMapperSimplifiedAsBackReference(
            parent,
            propertyName,
            propertyAccessor
        )
        else -> AnyToOnePropertyMapperSimplifiedWithJoinFetch(
            projectorFactory,
            parent,
            entityClass,
            projectionClass,
            projectionClassImpl,
            propertyName,
            hadJoinFetch
        )
    }

    override fun simplify(): Mapper = mapper
}