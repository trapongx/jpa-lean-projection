package com.runninglane.jpa.projection.mapper.elementcollection

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionFactory
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.mapper.embedded.EmbeddableClassMapper
import javax.persistence.Tuple
import javax.persistence.criteria.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

internal class EmbeddableElementCollectionPropertyMapper(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    private val propertyInfo: PropertyInfo,
    private val useJoinFetch: Boolean
) : Mapper, PropertyMapper {
    override val propertyName: String get() = propertyInfo.propertyName

    private val elementSrcType = propertyInfo.srcPropTypeArgType1!!
    private val elementType = propertyInfo.propTypeArgType1!!
    private val elementTypeImpl = projectorFactory.projectionFactory
        .getImplementation(elementSrcType, elementType)

    private val mapper: EmbeddableClassMapper? = if (useJoinFetch) {
        EmbeddableClassMapper(
            projectorFactory,
            this,
            elementSrcType,
            elementType,
            elementTypeImpl,
            useJoinFetch
        )
    } else null

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = listOfNotNull(mapper)

    override fun hasJoinFetch(): Boolean = useJoinFetch

    override fun buildSelections(
        path: Path<*>,
        tupleIndexCounter: TupleIndexCounter
    ): List<Expression<*>> {
        return if (useJoinFetch) {
            val associationPath: Join<Any, Any> = with(propertyInfo) {
                when {
                    isCollection || isList || isSet -> (path as From<*, *>).join(propertyName, JoinType.LEFT)
                    else -> error("Unsupported collection type $propertyType")
                }
            }
            mapper!!.buildSelections(associationPath, tupleIndexCounter)
        } else {
            emptyList()
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
            if (mapper!!.isAllTupleElementsNull(tuple)) {
                null to emptyList()
            } else {
                val item = elementTypeImpl.createInstance()
                val (fetchers, _) = mapper.readTuple(tuple, item, projection, projectionIdentityMap)
                item to fetchers
            }
        }
    }
}