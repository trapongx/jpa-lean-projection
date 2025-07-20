package com.runninglane.jpa.projection.facade

import kotlin.reflect.KClass

class FacadeFactory(
    annotationForPropertyInheriting: Set<KClass<Annotation>> = emptySet()
) {
    private val snapFacadeFactory = com.runninglane.facade.FacadeFactory(annotationForPropertyInheriting)

    fun <P : Any, E : Any> FacadeFactory.fromEntity(
        projectionClass: KClass<P>,
        entityClass: KClass<E>,
        entity: E
    ): P {
        return snapFacadeFactory.from(entityClass, entity)
            .to(projectionClass)
    }

    fun <E : Any, P : Any> toEntity(
        entityClass: KClass<E>,
        projectionClass: KClass<P>,
        projection: P
    ): E {
        return snapFacadeFactory.from(projectionClass, projection)
            .to(entityClass)
    }

    companion object {
        val default: FacadeFactory = FacadeFactory()
    }
}

inline fun <reified P : Any, reified E : Any> FacadeFactory.fromEntity(entity: E): P =
    fromEntity(P::class, E::class, entity)

inline fun <reified E : Any, reified P : Any> FacadeFactory.toEntity(projection: P): E =
    toEntity(E::class, P::class, projection)
