package com.runninglane.jpa.projection.facade

import com.runninglane.facade.FacadeFactory
import kotlin.reflect.KClass

fun <P : Any, E : Any> FacadeFactory.fromEntity(
    projectionClass: KClass<P>,
    entityClass: KClass<E>,
    entity: E
): P {
    return from(entityClass, entity)
        .to(projectionClass)
}

fun <E : Any, P : Any> FacadeFactory.toEntity(
    entityClass: KClass<E>,
    projectionClass: KClass<P>,
    projection: P
): E {
    return from(projectionClass, projection)
        .to(entityClass)
}


inline fun <reified P : Any, reified E : Any> FacadeFactory.fromEntity(entity: E): P =
    fromEntity(P::class, E::class, entity)

inline fun <reified E : Any, reified P : Any> FacadeFactory.toEntity(projection: P): E =
    toEntity(E::class, P::class, projection)
