package com.runninglane.jpa.projection.mapper.association

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.mapper.Fetcher
import com.runninglane.jpa.projection.mapper.Mapper
import com.runninglane.jpa.projection.mapper.PropertyAccessor
import com.runninglane.jpa.projection.mapper.PropertyMapper
import com.runninglane.jpa.projection.mapper.TupleIndexCounter
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path

internal class AnyToOnePropertyMapperSimplifiedAsBackReference(
    private val parent: Mapper,
    override val propertyName: String,
    private val propertyAccessor: PropertyAccessor
) : Mapper, PropertyMapper {
    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = emptyList()

    override fun hasJoinFetch(): Boolean = false

    override fun buildSelections(path: Path<*>, tupleIndexCounter: TupleIndexCounter): List<Expression<*>> = emptyList()

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        propertyAccessor.set(projection, parentProjection)
        return emptyList<Fetcher>() to null
    }
}