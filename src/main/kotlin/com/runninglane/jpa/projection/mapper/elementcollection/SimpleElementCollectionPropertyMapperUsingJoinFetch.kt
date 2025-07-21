package com.runninglane.jpa.projection.mapper.elementcollection

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.mapper.*
import javax.persistence.Tuple
import javax.persistence.criteria.*

internal class SimpleElementCollectionPropertyMapperUsingJoinFetch(
    private val parent: Mapper,
    private val propertyInfo: PropertyInfo,
) : Mapper, PropertyMapper {
    var tupleIndex: Int = -1
        private set

    override val propertyName: String get() = propertyInfo.propertyName

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = emptyList()

    override fun hasJoinFetch(): Boolean = true

    override fun buildSelections(
        path: Path<*>,
        tupleIndexCounter: TupleIndexCounter
    ): List<Expression<*>> {
        return with(propertyInfo) {
            val associationPath: Join<Any, Any> = when {
                isCollection || isList || isSet -> (path as From<*, *>).join(propertyName, JoinType.LEFT)
                else -> error("Unsupported collection type $propertyType")
            }
            tupleIndex = tupleIndexCounter.next()
            listOf(associationPath)
        }
    }

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        return readTupleIntoCollection(propertyInfo, projection, parentProjection) {
            tuple.get(tupleIndex) to emptyList()
        }
    }

    override fun isAllTupleElementsNull(tuple: Tuple): Boolean = tuple.get(tupleIndex) == null
}
