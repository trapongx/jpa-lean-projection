package com.runninglane.jpa.projection.mapper

import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import javax.persistence.EntityManager
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal class EntityPropertyFetcher(
    private val projectorFactory: ProjectorFactory,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    private val propertyName: String,
    private val projections: List<Any>
) : Fetcher {

    val idProp: KProperty1<*, *> = getProjectionIdProp(projectionClassImpl, entityClass)

    override fun fetch(entityManager: EntityManager, projectionIdentityMap: ProjectionIdentityMap): List<Fetcher> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createTupleQuery()
        val root = query.from(entityClass.java)
        val projector = projectorFactory.getOrCreate(entityClass, projectionClass, setOf(idProp.name, propertyName))
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
        override fun reduce(fetchers: List<Fetcher>): List<EntityPropertyFetcher> {
            @Suppress("UNCHECKED_CAST")
            fetchers as List<EntityPropertyFetcher>

            return fetchers.groupBy {
                Triple(it.entityClass, it.projectionClass, it.propertyName)
            }.values.map { similarFetchers ->
                if (similarFetchers.size == 1) {
                    similarFetchers.single()
                } else {
                    val sample = similarFetchers.first()
                    EntityPropertyFetcher(
                        sample.projectorFactory,
                        sample.entityClass,
                        sample.projectionClass,
                        sample.projectionClassImpl,
                        sample.propertyName,
                        similarFetchers.flatMap { it.projections }
                    )
                }
            }
        }
    }
}