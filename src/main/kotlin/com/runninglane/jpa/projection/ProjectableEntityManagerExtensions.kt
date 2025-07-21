package com.runninglane.jpa.projection

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.jpa.projection.builder.ProjectionQueryBuilder
import javax.persistence.EntityManager
import javax.persistence.criteria.*

fun EntityManager.projectable(
    projectorFactory: ProjectorFactory = ProjectorFactory(ProjectionFactory())
) = when (this) {
    is ProjectableEntityManager -> this
    else -> ProjectableEntityManagerImpl(projectorFactory, this)
}

fun EntityManager.projectable(dtoBuddy: DtoBuddy) = when (this) {
    is ProjectableEntityManager -> {
        require(this.projectorFactory.projectionFactory.dtoBuddy == dtoBuddy) {
            "The DtoBuddy instance passed in is not the same as the one used by the ProjectableEntityManager."
        }
        this
    }
    else -> {
        val projectorFactory = ProjectorFactory(ProjectionFactory(dtoBuddy))
        ProjectableEntityManagerImpl(projectorFactory, this)
    }
}

inline fun <reified E : Any, reified P : Any> ProjectableEntityManager.queryWithProjection(
    noinline predicateBuilder: ((cb: CriteriaBuilder, query: CriteriaQuery<*>, root: Root<E>) -> Predicate?)? = null,
    noinline ordersBuilder: ((cb: CriteriaBuilder, root: Root<E>) -> Array<Order>?)? = null,
    firstResult: Int? = null,
    maxResults: Int? = null
): List<P> = queryWithProjection(E::class, P::class, predicateBuilder, ordersBuilder, firstResult, maxResults)


fun <E : Any, P : Any> ProjectableEntityManager.createProjectionQuery(projector: Projector<E, P>)
        : ProjectionQueryBuilder<E, P> =
    ProjectionQueryBuilder(projector, this)

inline fun <reified E : Any, reified P : Any> ProjectableEntityManager.createProjectionQuery(projectedPropertyNames: Set<String>? = null)
        : ProjectionQueryBuilder<E, P> =
    ProjectionQueryBuilder(createProjector(projectedPropertyNames), this)

inline fun <reified E : Any, reified P : Any> ProjectableEntityManager.createProjector(
    projectedPropertyNames: Set<String>? = null
): Projector<E, P> = Projector(projectorFactory, E::class, P::class, projectedPropertyNames)