package com.runninglane.jpa.projection.mapper

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path

internal interface Mapper {
    fun getParent(): Mapper?
    fun getChildren(): List<Mapper>
    fun hasJoinFetch(): Boolean
    fun buildSelections(
        path: Path<*>,
        tupleIndexCounter: TupleIndexCounter = TupleIndexCounter()
    ): List<Expression<*>>
    fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?>
    fun isAllTupleElementsNull(tuple: Tuple): Boolean = getChildren().all { it.isAllTupleElementsNull(tuple) }
}
