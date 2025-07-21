package com.runninglane.jpa.projection

import kotlin.reflect.KClass

class ProjectorFactory(val projectionFactory: ProjectionFactory) {
    private val projectorCache: MutableMap<Triple<KClass<*>, KClass<*>, Set<String>>, Projector<*, *>> = mutableMapOf()

    fun <E : Any, P : Any> getOrCreate(
        entityClass: KClass<E>,
        projectionClass: KClass<P>,
        projectedPropertyNames: Set<String>? = null
    ): Projector<E, P>  {
        val key = Triple(entityClass, projectionClass, projectedPropertyNames ?: emptySet())
        @Suppress("UNCHECKED_CAST")
        return projectorCache.getOrPut(key) {
            Projector(this, entityClass, projectionClass, projectedPropertyNames)
        } as Projector<E, P>
    }
}