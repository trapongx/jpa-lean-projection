package com.runninglane.jpa.projection.mapper

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

internal class EntityPropertyMapperUsingFetcher(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    private val propertyInfo: PropertyInfo
) : Mapper {

    init {
        assert(
            projectionClass != projectionClassImpl
                    && !projectionClassImpl.isAbstract
                    && projectionClassImpl.isSubclassOf(projectionClass)
        )
    }

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = emptyList()

    override fun hasJoinFetch(): Boolean = false

    override fun buildSelections(
        path: Path<*>,
        tupleIndexCounter: TupleIndexCounter
    ): List<Expression<*>> = emptyList()

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        val fetcher = EntityPropertyFetcher(
            projectorFactory,
            entityClass,
            projectionClass,
            projectionClassImpl,
            propertyInfo.propertyName,
            listOf(projection)
        )
        return listOf(fetcher) to null
    }
}