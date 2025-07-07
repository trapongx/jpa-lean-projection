package com.runninglane.jpa.projection.mapper.sametype

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.mapper.Fetcher
import com.runninglane.jpa.projection.mapper.Mapper
import com.runninglane.jpa.projection.mapper.TupleIndexCounter
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path

internal abstract class SameTypeMapper(
    private val parent: Mapper?,
) : Mapper {
    var tupleIndex: Int = -1
        private set

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = emptyList()

    override fun hasJoinFetch(): Boolean = false

    abstract fun buildValueExpression(path: Path<*>): Expression<*>

    override fun buildSelections(path: Path<*>, tupleIndexCounter: TupleIndexCounter): List<Expression<*>> {
        tupleIndex = tupleIndexCounter.next()
        return listOf(buildValueExpression(path))
    }

    abstract fun storeValue(projection: Any, value: Any?)

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        val value = tuple.get(tupleIndex)
        storeValue(projection, value)
        return emptyList<Fetcher>() to null
    }

    override fun isAllTupleElementsNull(tuple: Tuple): Boolean = tuple.get(tupleIndex) == null
}