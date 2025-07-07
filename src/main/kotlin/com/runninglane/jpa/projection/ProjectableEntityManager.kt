package com.runninglane.jpa.projection

import javax.persistence.EntityManager
import javax.persistence.criteria.*
import kotlin.reflect.KClass

interface ProjectableEntityManager : EntityManager {

    val projectorFactory: ProjectorFactory

    fun <E : Any, P : Any> queryWithProjection(
        entityClass: KClass<E>,
        projectionClass: KClass<P>,
        predicateBuilder: ((cb: CriteriaBuilder, query: CriteriaQuery<*>, root: Root<E>) -> Predicate?)? = null,
        ordersBuilder: ((cb: CriteriaBuilder, root: Root<E>) -> Array<Order>?)? = null,
        firstResult: Int? = null,
        maxResults: Int? = null
    ): List<P>

    fun <E : Any, P : Any> queryWithProjection(
        projector: Projector<E, P>,
        predicateBuilder: ((cb: CriteriaBuilder, query: CriteriaQuery<*>, root: Root<E>) -> Predicate?)? = null,
        ordersBuilder: ((cb: CriteriaBuilder, root: Root<E>) -> Array<Order>?)? = null,
        firstResult: Int? = null,
        maxResults: Int? = null
    ): List<P>
}

