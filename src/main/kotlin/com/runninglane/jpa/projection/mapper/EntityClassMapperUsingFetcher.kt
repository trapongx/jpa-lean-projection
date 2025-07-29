package com.runninglane.jpa.projection.mapper

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.assert.ProjectionClassAssertion
import com.runninglane.jpa.projection.mapper.sametype.SameTypePropertyMapper
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal class EntityClassMapperUsingFetcher(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper?,
    val entityClass: KClass<*>,
    val projectionClass: KClass<*>,
    override val projectionClassImpl: KClass<*>
) : BaseEntityClassMapper {

    init {
        assert(ProjectionClassAssertion.isCorrectSemantics(entityClass, projectionClass, projectionClassImpl))
    }

    val idProp: KProperty1<*, *> = getProjectionIdProp(projectionClass, entityClass)

    private val idMapper = SameTypePropertyMapper(this, entityClass, projectionClassImpl, idProp.name)

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = listOf(idMapper)

    override fun hasJoinFetch(): Boolean = false

    override fun buildSelections(
        path: Path<*>,
        tupleIndexCounter: TupleIndexCounter
    ): List<Expression<*>> = idMapper.buildSelections(path, tupleIndexCounter)

    override fun readId(tuple: Tuple): Any? = tuple[idMapper.tupleIndex]

    override fun isIdNull(tuple: Tuple): Boolean = tuple.get(idMapper.tupleIndex) == null

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        val fetcher: Fetcher? = run {
            if (isIdNull(tuple) || projectionIdentityMap.isFetched(projection))
                return@run null

            idMapper.readTuple(tuple, projection, parentProjection, projectionIdentityMap)

            EntityClassFetcher(projectorFactory, entityClass, projectionClass, listOf(projection)).also {
                projectionIdentityMap.rememberFetched(projection)
            }
        }

        return listOfNotNull(fetcher) to null
    }
}