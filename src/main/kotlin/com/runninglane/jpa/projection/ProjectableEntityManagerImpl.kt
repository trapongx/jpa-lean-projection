package com.runninglane.jpa.projection

import javax.persistence.EntityManager
import javax.persistence.criteria.*
import kotlin.reflect.KClass

class ProjectableEntityManagerImpl(
    override val projectorFactory: ProjectorFactory,
    delegate: EntityManager
) : ProjectableEntityManager, EntityManager by delegate {

    override fun <E : Any, P : Any> queryWithProjection(
        entityClass: KClass<E>,
        projectionClass: KClass<P>,
        predicateBuilder: ((cb: CriteriaBuilder, query: CriteriaQuery<*>, root: Root<E>) -> Predicate?)?,
        ordersBuilder: ((cb: CriteriaBuilder, root: Root<E>) -> Array<Order>?)?,
        firstResult: Int?,
        maxResults: Int?
    ): List<P> {
        val projector = projectorFactory.getOrCreate(entityClass, projectionClass)
        return queryWithProjection(projector, predicateBuilder, ordersBuilder, firstResult, maxResults)
    }

    override fun <E : Any, P : Any> queryWithProjection(
        projector: Projector<E, P>,
        predicateBuilder: ((cb: CriteriaBuilder, query: CriteriaQuery<*>, root: Root<E>) -> Predicate?)?,
        ordersBuilder: ((cb: CriteriaBuilder, root: Root<E>) -> Array<Order>?)?,
        firstResult: Int?,
        maxResults: Int?
    ): List<P> {
        val cb = criteriaBuilder
        val query = cb.createTupleQuery()
        val root = query.from(projector.entityClass.java)
        query.multiselect(*projector.buildSelections(root))
        predicateBuilder?.invoke(cb, query, root)?.also { predicate -> query.where(predicate) }
        ordersBuilder?.invoke(cb, root)?.takeIf { it.isNotEmpty() }?.also { orders -> query.orderBy(*orders) }
        return createQuery(query).also { cq ->
            firstResult?.also { cq.firstResult = it }
            maxResults?.also { cq.maxResults = it }
        }.resultList.projected(projector, this)
    }

}