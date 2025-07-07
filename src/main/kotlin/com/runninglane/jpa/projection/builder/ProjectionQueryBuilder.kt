package com.runninglane.jpa.projection.builder

import com.runninglane.jpa.projection.ProjectableEntityManager
import com.runninglane.jpa.projection.Projector
import javax.persistence.criteria.*

class ProjectionQueryBuilder<E : Any, P : Any>(
    private val projector: Projector<E, P>,
    private val entityManager: ProjectableEntityManager
) {
    private var predicateBuilder: ((cb: CriteriaBuilder, query: CriteriaQuery<*>, root: Root<E>) -> Predicate?)? = null

    private var ordersBuilder: ((cb: CriteriaBuilder, root: Root<E>) -> Array<Order>?)? = null

    private var firstResult: Int? = null

    private var maxResults: Int? = null

    fun where(predicateBuilder: ((cb: CriteriaBuilder, query: CriteriaQuery<*>, root: Root<E>) -> Predicate?)? = null) = apply {
        this.predicateBuilder = predicateBuilder
    }

    fun whereMatchExample(vararg criteria: Pair<String, Any?>) = apply {
        if (criteria.isEmpty()) {
            predicateBuilder = null
        } else {
            where { cb, _, root ->
                cb.and(
                    *criteria.map {
                        cb.equal(root.get<Any>(it.first), it.second)
                    }.toTypedArray()
                )
            }
        }
    }

    fun orderBy(ordersBuilder: ((cb: CriteriaBuilder, root: Root<E>) -> Array<Order>?)? = null) = apply {
        this.ordersBuilder = ordersBuilder
    }

    fun firstResult(firstResult: Int?) = apply {
        this.firstResult = firstResult
    }

    fun maxResults(maxResults: Int?) = apply {
        this.maxResults = maxResults
    }

    val resultList: List<P> get() {
        return entityManager.queryWithProjection(
            projector,
            predicateBuilder,
            ordersBuilder,
            firstResult,
            maxResults
        )
    }
}