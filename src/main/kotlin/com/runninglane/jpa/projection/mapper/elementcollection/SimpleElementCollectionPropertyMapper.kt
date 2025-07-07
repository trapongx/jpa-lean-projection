package com.runninglane.jpa.projection.mapper.elementcollection

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import javax.persistence.Tuple
import javax.persistence.criteria.*
import kotlin.reflect.KClass

internal class SimpleElementCollectionPropertyMapper(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    private val propertyInfo: PropertyInfo,
    private val useJoinFetch: Boolean
) : Mapper, PropertyMapper {
    var tupleIndex: Int = -1
        private set

    override val propertyName: String get() = propertyInfo.propertyName

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = emptyList()

    override fun hasJoinFetch(): Boolean = useJoinFetch

    override fun buildSelections(
        path: Path<*>,
        tupleIndexCounter: TupleIndexCounter
    ): List<Expression<*>> {
        return with(propertyInfo) {
            if (useJoinFetch) {
                val associationPath: Join<Any, Any> = when {
                    isCollection || isList || isSet -> (path as From<*, *>).join(propertyName, JoinType.LEFT)
                    else -> error("Unsupported collection type $propertyType")
                }
                tupleIndex = tupleIndexCounter.next()
                listOf(associationPath)
            } else {
                emptyList()
            }
        }
    }

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        if (!useJoinFetch) {
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

        return readTupleIntoCollection(propertyInfo, projection, parentProjection) {
            tuple.get(tupleIndex) to emptyList()
        }
    }

    override fun isAllTupleElementsNull(tuple: Tuple): Boolean = tuple.get(tupleIndex) == null
}
