package com.runninglane.jpa.projection.mapper

import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import javax.persistence.EntityManager
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal class EntityClassFetcher(
    private val projectorFactory: ProjectorFactory,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projections: List<Any>
) : Fetcher {

    val idProp: KProperty1<*, *> = getProjectionIdProp(projectionClass, entityClass)

    override fun fetch(entityManager: EntityManager, projectionIdentityMap: ProjectionIdentityMap): List<Fetcher> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createTupleQuery()
        val root = query.from(entityClass.java)
        val projector = projectorFactory.getOrCreate(entityClass, projectionClass)
        query.multiselect(*projector.buildSelections(root))
        val projectionIds = projections.map { idProp.call(it) }.distinct()
        query.where(
            root.get<Any>(idProp.name).`in`(projectionIds)
        )
        val tuples = entityManager.createQuery(query).resultList
        val (_, fetchers) = projector.readTuples(tuples, entityManager, projectionIdentityMap, true)
        return fetchers
    }

    override fun getReducer(): Fetcher.Reducer = Reducer

    private object Reducer : Fetcher.Reducer {
        override fun reduce(fetchers: List<Fetcher>): List<EntityClassFetcher> {
            @Suppress("UNCHECKED_CAST")
            fetchers as List<EntityClassFetcher>

            return fetchers.groupBy {
                Pair(it.entityClass, it.projectionClass)
            }.values.map { similarFetchers ->
                if (similarFetchers.size == 1) {
                    similarFetchers.single()
                } else {
                    val sample = similarFetchers.first()
                    EntityClassFetcher(
                        sample.projectorFactory,
                        sample.entityClass,
                        sample.projectionClass,
                        similarFetchers.flatMap { it.projections }
                    )
                }
            }
        }
    }
}