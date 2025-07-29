package com.runninglane.jpa.projection.mapper.map

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.mapper.assert.ProjectionClassAssertion
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path
import kotlin.reflect.KClass

internal class MapPropertyMapperUsingFetcher(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    private val propertyInfo: PropertyInfo
) : Mapper, PropertyMapper {

    init {
        assert(ProjectionClassAssertion.isCorrectSemantics(entityClass, projectionClass, projectionClassImpl))
    }

    override val propertyName: String get() = propertyInfo.propertyName

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
        if (projectionIdentityMap.isFetched(projection to propertyInfo.propertyName))
            return emptyList<Fetcher>() to null

        val fetcher = MapPropertyFetcher(
            projectorFactory,
            entityClass,
            projectionClass,
            projectionClassImpl,
            propertyInfo.propertyName,
            propertyInfo,
            listOf(projection)
        )
        projectionIdentityMap.rememberFetched(projection to propertyInfo.propertyName)
        return listOf(fetcher) to null
    }
}