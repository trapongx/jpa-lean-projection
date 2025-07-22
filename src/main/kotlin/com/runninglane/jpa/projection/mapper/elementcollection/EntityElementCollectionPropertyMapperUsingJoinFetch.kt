package com.runninglane.jpa.projection.mapper.elementcollection

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.mapper.sametype.SameTypeElementHolder
import com.runninglane.jpa.projection.mapper.sametype.SameTypeElementMapper
import javax.persistence.Tuple
import javax.persistence.criteria.*

internal class EntityElementCollectionPropertyMapperUsingJoinFetch(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val propertyInfo: PropertyInfo,
) : Mapper, PropertyMapper {

    override val propertyName: String get() = propertyInfo.propertyName

    private val mapper: Mapper = if (propertyInfo.propTypeArgType1 == propertyInfo.srcPropTypeArgType1) {
        SameTypeElementMapper(this)
    } else {
        EntityClassMapper.of(
            projectorFactory,
            this,
            propertyInfo.srcPropTypeArgType1!!,
            propertyInfo.propTypeArgType1!!,
            true,
            null
        )
    }

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = listOf(mapper)

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
            when (mapper) {
                is SameTypeElementMapper -> {
                    val holder = SameTypeElementHolder()
                    val (fetchers, _) = mapper.readTuple(tuple, holder, projection, projectionIdentityMap)
                    holder.value to fetchers
                }

                is BaseEntityClassMapper -> {
                    if (mapper.isIdNull(tuple)) {
                        null to emptyList()
                    } else {
                        val item = projectorFactory.projectionFactory.create(
                            propertyInfo.srcPropTypeArgType1!!, propertyInfo.propTypeArgType1!!
                        )
                        val (fetchers, _) = mapper.readTuple(tuple, item, projection, projectionIdentityMap)
                        item to fetchers
                    }
                }

                else -> error("Should not happen: ${mapper::class.qualifiedName} is not a BaseEntityClassMapper or SameTypeElementMapper")
            }
        }
    }
}