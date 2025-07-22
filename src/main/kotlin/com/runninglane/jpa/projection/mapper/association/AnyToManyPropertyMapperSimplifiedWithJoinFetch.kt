package com.runninglane.jpa.projection.mapper.association

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.mapper.sametype.SameTypeElementHolder
import com.runninglane.jpa.projection.mapper.sametype.SameTypeElementMapper
import com.runninglane.jpa.projection.reflection.annotatedWith
import com.runninglane.jpa.projection.reflection.getAnnotation
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

internal class AnyToManyPropertyMapperSimplifiedWithJoinFetch(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val propertyInfo: PropertyInfo,
    private val entityClassOnRightSide: KClass<*>,
    private val projectionClassOnRightSide: KClass<*>,
    private val associationPathBuilder: (Path<*>) -> Path<*>
) : Mapper, PropertyMapper, ProbablyInvertible {
    override fun getParent(): Mapper? = parent

    override val propertyName: String get() = propertyInfo.propertyName

    private val projectionClassOnRightSideImpl = if (projectionClassOnRightSide == entityClassOnRightSide) {
        projectionClassOnRightSide
    } else {
        projectorFactory.projectionFactory
            .getImplementation(entityClassOnRightSide, projectionClassOnRightSide)
    }

    private val mapper: Mapper = if (projectionClassOnRightSide == entityClassOnRightSide) {
        SameTypeElementMapper(this)
    } else {
        EntityClassMapper.of(
            projectorFactory,
            this,
            entityClassOnRightSide,
            projectionClassOnRightSide,
            true,
            null
        )
    }

    override fun getChildren(): List<Mapper> = listOf(mapper)

    override fun hasJoinFetch(): Boolean = true

    override fun buildSelections(
        path: Path<*>,
        tupleIndexCounter: TupleIndexCounter
    ): List<Expression<*>> =
        mapper.buildSelections(associationPathBuilder(path), tupleIndexCounter)

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        return readTupleIntoCollection(propertyInfo, projection, parentProjection) {
            when (mapper) {
                is SameTypeElementMapper -> {
                    val holder = SameTypeElementHolder()
                    val (fetchers, _) = mapper.readTuple(tuple, holder, projection, projectionIdentityMap)
                    holder.value to fetchers
                }

                is BaseEntityClassMapper -> {
                    if (mapper.isIdNull(tuple)) {
                        null to emptyList()
                    } else {
                        val id = mapper.readId(tuple)!!
                        val reusableInstance =
                            projectionIdentityMap.get(entityClassOnRightSide, projectionClassOnRightSideImpl, id)

                        when (reusableInstance) {
                            null -> {
                                val newInstance = projectorFactory.projectionFactory
                                    .create(entityClassOnRightSide, projectionClassOnRightSide)
                                    .also {
                                        projectionIdentityMap.add(
                                            entityClassOnRightSide,
                                            projectionClassOnRightSideImpl,
                                            id,
                                            it
                                        )
                                    }
                                val (fetchers, _) = mapper.readTuple(
                                    tuple,
                                    newInstance,
                                    projection,
                                    projectionIdentityMap
                                )
                                newInstance to fetchers
                            }

                            else -> reusableInstance to emptyList()
                        }
                    }
                }

                else -> error("Should not happen: ${mapper::class.qualifiedName} is not a BaseEntityClassMapper or SameTypeElementMapper")
            }
        }
    }

    override fun checkAssociationInvertibility(
        entityClassOnRightSide: KClass<*>,
        projectionClassOnRightSide: KClass<*>,
        propertyPath: String
    ): ProbablyInvertible.AssociationInvertibilityCheckResult {
        val (otherSrcPropType, otherSrcProp) = propertyPath.split('.')
            .fold(entityClassOnRightSide to null as KProperty1<*, *>?) { (currentClass, _), name ->
                val nextProp = currentClass.memberProperties.first { it.name == name }
                nextProp.returnType.jvmErasure to nextProp
            }

        val otherPropType: KClass<*> = propertyPath.split('.')
            .fold(projectionClassOnRightSide) { currentClass, name ->
                currentClass.memberProperties
                    .first { it.name == name }
                    .returnType.jvmErasure
            }

        val srcProp = entityClass.memberProperties.first { it.name == propertyInfo.propertyName }

        val checkMappedBy by lazy {
            srcProp.getAnnotation<OneToMany>()?.let { it.mappedBy == propertyPath } == true
                    && otherSrcProp?.annotatedWith<ManyToOne>() == true
        }

        val isInversion = otherSrcPropType == entityClass
                && entityClassOnRightSide == propertyInfo.srcPropTypeArgType1
                && checkMappedBy

        val isProjectionTypeCompatible = isInversion
                && projectionClassOnRightSide.isSubclassOf(propertyInfo.propTypeArgType1!!)
                && projectionClass.isSubclassOf(otherPropType)

        return ProbablyInvertible.AssociationInvertibilityCheckResult(isInversion, isProjectionTypeCompatible)
    }

}