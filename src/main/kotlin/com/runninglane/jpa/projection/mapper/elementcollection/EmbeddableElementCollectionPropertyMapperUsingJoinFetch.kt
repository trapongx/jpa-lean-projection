package com.runninglane.jpa.projection.mapper.elementcollection

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.mapper.embedded.EmbeddableClassMapper
import javax.persistence.Tuple
import javax.persistence.criteria.*
import kotlin.reflect.full.createInstance

internal class EmbeddableElementCollectionPropertyMapperUsingJoinFetch(
    projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val propertyInfo: PropertyInfo,
) : Mapper, PropertyMapper {
    override val propertyName: String get() = propertyInfo.propertyName

    private val elementSrcType = propertyInfo.srcPropTypeArgType1!!
    private val elementType = propertyInfo.propTypeArgType1!!
    private val elementTypeImpl = projectorFactory.projectionFactory
        .getImplementation(elementSrcType, elementType)

    private val mapper: EmbeddableClassMapper = EmbeddableClassMapper(
        projectorFactory,
        this,
        elementSrcType,
        elementType,
        elementTypeImpl,
        true
    )

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = listOfNotNull(mapper)

    override fun hasJoinFetch(): Boolean = true

    override fun buildSelections(
        path: Path<*>,
        tupleIndexCounter: TupleIndexCounter
    ): List<Expression<*>> {
        val associationPath: Join<Any, Any> = with(propertyInfo) {
            when {
                isCollection || isList || isSet -> (path as From<*, *>).join(propertyName, JoinType.LEFT)
                else -> error("Unsupported collection type $propertyType")
            }
        }
        return mapper.buildSelections(associationPath, tupleIndexCounter)
    }

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        return readTupleIntoCollection(propertyInfo, projection, parentProjection) {
            if (mapper.isAllTupleElementsNull(tuple)) {
                null to emptyList()
            } else {
                val item = elementTypeImpl.createInstance()
                val (fetchers, _) = mapper.readTuple(tuple, item, projection, projectionIdentityMap)
                item to fetchers
            }
        }
    }
}