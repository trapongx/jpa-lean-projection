package com.runninglane.jpa.projection.mapper.map

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import javax.persistence.Tuple
import javax.persistence.criteria.*

/**
 * This mapper is used with an assumption that the property type is a Map with no type difference in neither key nor value.
 */
@Suppress("UNCHECKED_CAST")
internal class MapPropertyMapperSimplifiedWithJoinFetch(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val propertyInfo: PropertyInfo
) : Mapper, PropertyMapper {

    override val propertyName: String get() = propertyInfo.propertyName

    private val keyMapper = SameTypeMapKeyOrValueMapper(this, true)

    private val valueMapper = SameTypeMapKeyOrValueMapper(this, false)

    val mappers: List<Mapper> = listOf(keyMapper, valueMapper)

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = mappers

    override fun hasJoinFetch(): Boolean = true

    override fun buildSelections(
        path: Path<*>,
        tupleIndexCounter: TupleIndexCounter
    ): List<Expression<*>> {
        val associationPath: MapJoin<Any, Any, Any> =
            (path as From<*, *>).joinMap(propertyInfo.propertyName, JoinType.LEFT)
        return keyMapper.buildSelections(associationPath.key(), tupleIndexCounter) +
                valueMapper.buildSelections(associationPath.value(), tupleIndexCounter)
    }

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        return readTupleIntoMap(propertyInfo, projection, parentProjection) {
            val entry = MapEntry()
            val (keyFetchers, _) = keyMapper.readTuple(tuple, entry, projection, projectionIdentityMap)
            if (entry.key != null) {
                val (valueFetchers, _) = valueMapper.readTuple(tuple, entry, projection, projectionIdentityMap)
                Pair(entry.key!!, entry.value) to (keyFetchers + valueFetchers)
            } else {
                null to emptyList()
            }
        }
    }
}