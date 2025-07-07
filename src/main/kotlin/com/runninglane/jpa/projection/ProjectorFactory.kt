package com.runninglane.jpa.projection

import kotlin.reflect.KClass

class ProjectorFactory(val projectionFactory: ProjectionFactory) {
    private val projectorCache: MutableMap<Pair<KClass<*>, KClass<*>>, Projector<*, *>> = mutableMapOf()

    fun <E : Any, P : Any> getOrCreate(entityClass: KClass<E>, projectionClass: KClass<P>): Projector<E, P>  {
        @Suppress("UNCHECKED_CAST")
        return projectorCache.getOrPut(entityClass to projectionClass) {
            Projector(this, entityClass, projectionClass)
        } as Projector<E, P>
    }
}