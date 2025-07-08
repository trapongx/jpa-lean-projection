package com.runninglane.jpa.projection

import javax.persistence.EntityManager
import kotlin.reflect.KClass

@JvmSynthetic
fun <E : Any, P : Any> EntityManager.queryWithProjection(
    entityClass: KClass<E>,
    projectionClass: KClass<P>,
): List<P> {
    TODO("Not yet implemented")
}

@JvmSynthetic
inline fun <reified E : Any, reified P : Any> EntityManager.queryWithProjection() =
    queryWithProjection(E::class, P::class)