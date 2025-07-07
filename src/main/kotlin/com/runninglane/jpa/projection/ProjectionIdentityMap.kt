package com.runninglane.jpa.projection

import kotlin.reflect.KClass

internal class ProjectionIdentityMap {

    private val map: MutableMap<Triple<KClass<*>, KClass<*>, Any>, Any> = mutableMapOf()

    fun add(entityClass: KClass<*>, projectionClass: KClass<*>, id: Any, projection: Any) {
        assert(!projectionClass.isAbstract) {
            "Expected projection class to be concrete"
        }
        require(projectionClass.java.isAssignableFrom(projection::class.java)) {
            "Projection class and instance type mismatched"
        }
        val key = Triple(entityClass, projectionClass, id)
        val existing = map[key]
        require(existing == null || projection === existing) {
            "Other projection instance with the same key already exists." +
                    " entityClass: $entityClass, projection: $projectionClass, id: $id."
        }
        if (existing == null) map[key] = projection
    }

    fun get(entityClass: KClass<*>, projectionClass: KClass<*>, id: Any): Any? {
        assert(!projectionClass.isAbstract) {
            "Expected projection class to be concrete"
        }
        return map[Triple(entityClass, projectionClass, id)]
    }
}