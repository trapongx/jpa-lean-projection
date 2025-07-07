package com.runninglane.jpa.projection.mapper

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path

internal interface SimplifiableMapper : Mapper {

    companion object {
        val exceptionUsingUnsimplifiedMapper = UnsupportedOperationException(
            "Should use the simplified version of this mapper. Trying to use it is considered a bug."
        )
    }

    fun simplify(): Mapper

    override fun getParent(): Mapper? = throw exceptionUsingUnsimplifiedMapper

    override fun getChildren(): List<Mapper> = throw exceptionUsingUnsimplifiedMapper

    override fun hasJoinFetch(): Boolean = throw exceptionUsingUnsimplifiedMapper

    override fun buildSelections(path: Path<*>, tupleIndexCounter: TupleIndexCounter): List<Expression<*>> {
        throw exceptionUsingUnsimplifiedMapper
    }

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        throw exceptionUsingUnsimplifiedMapper
    }

}