package com.runninglane.jpa.projection

import com.runninglane.jpa.projection.mapper.EntityClassMapper
import com.runninglane.jpa.projection.mapper.Fetcher
import com.runninglane.jpa.projection.mapper.reduce
import javax.persistence.EntityManager
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path
import kotlin.reflect.KClass

class Projector<E : Any, P : Any>(
    private val projectorFactory: ProjectorFactory,
    val entityClass: KClass<E>,
    val projectionClass: KClass<P>,
    projectedPropertyNames: Set<String>? = null
) {
    private val mapper: EntityClassMapper = EntityClassMapper.of(
        projectorFactory,
        entityClass,
        projectionClass,
        false,
        projectedPropertyNames
    )

    fun buildSelections(path: Path<*>): Array<Expression<*>> =
        mapper.buildSelections(path).toTypedArray()

    @Suppress("UNCHECKED_CAST")
    fun readTuples(tuples: List<Tuple>, entityManager: EntityManager, projectionPostProcessor: ((Any) -> Unit)? = null): List<P> =
        readTuples(tuples, entityManager, ProjectionIdentityMap(), false, projectionPostProcessor).first as List<P>

    /**
     * If providedProjections is not null, all of its elements are assumed to exist in projectionBank.
     */
    internal fun readTuples(
        tuples: List<Tuple>,
        entityManager: EntityManager,
        projectionIdentityMap: ProjectionIdentityMap,
        isFetching: Boolean,
        projectionPostProcessor: ((Any) -> Unit)? = null
    ): Pair<List<Any>, List<Fetcher>> {
        val projections = mutableListOf<Any>()

        val fetchers = mutableListOf<Fetcher>()

        fun commonReadTuple(tuple: Tuple, consumeMapperReadTupleResult: (Pair<List<Fetcher>, HydrationMaterial?>) -> Unit) {
            val id = mapper.readId(tuple)!!
            val projection = projectionIdentityMap.get(entityClass, mapper.projectionClassImpl, id)
                ?: projectorFactory.projectionFactory.create(entityClass, mapper.projectionClassImpl).also {
                    projectionIdentityMap.add(entityClass, mapper.projectionClassImpl, id, it)
                }
            mapper.readTuple(tuple, projection, null, projectionIdentityMap)
                .also { consumeMapperReadTupleResult(it) }
            projections.add(projection)
        }

        if (mapper.hasJoinFetch()) {
            val hydrator = Hydrator.create(mapper).newHydration()
            var hydrationMaterial: HydrationMaterial? = null
            for (tuple in tuples) {
                val (isHydrated, fetchersFromHydration) = hydrator.hydrate(tuple, hydrationMaterial, projectionIdentityMap)
                if (isHydrated) {
                    fetchers.addAll(fetchersFromHydration)
                } else {
                    commonReadTuple(tuple) {
                        fetchers.addAll(it.first)
                        hydrationMaterial = it.second
                    }
                }
            }
        } else {
            for (tuple in tuples) {
                commonReadTuple(tuple) {
                    fetchers.addAll(it.first)
                }
            }
        }

        return if (isFetching) {
            emptyList<P>() to fetchers.toList()
        } else {
            fetchers.reduce()
                .also { initialFetchers ->
                    generateSequence(initialFetchers) { currentFetchers ->
                        currentFetchers.takeIf { it.isNotEmpty() }
                            ?.flatMap { it.fetch(entityManager, projectionIdentityMap) }
                            ?.reduce()
                    }.last()
                }

            projectionPostProcessor?.also { projectionIdentityMap.postProcessProjections(it) }

            projections.toList() to emptyList()
        }
    }
}

fun <P : Any> List<Tuple>.projected(projector: Projector<*, P>, entityManager: EntityManager, projectionPostProcessor: ((Any) -> Unit)? = null): List<P> =
    projector.readTuples(this, entityManager, projectionPostProcessor)