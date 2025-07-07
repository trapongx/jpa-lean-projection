package com.runninglane.jpa.projection.mapper

import com.runninglane.jpa.projection.reflection.annotatedWith
import com.runninglane.jpa.projection.HydrationMaterial
import javax.persistence.EmbeddedId
import javax.persistence.Id
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

internal fun List<Pair<List<Fetcher>, HydrationMaterial?>>.flatten() =
    flatMap { it.first } to firstNotNullOfOrNull { it.second }

internal fun readTupleIntoCollection(
    propertyInfo: PropertyInfo,
    projection: Any,
    parentProjection: Any?,
    readLogic: () -> Pair<Any?, List<Fetcher>>
): Pair<List<Fetcher>, HydrationMaterial?> {
    return with(propertyInfo) {
        @Suppress("UNCHECKED_CAST")
        val existingCollection = propertyInfo.accessor.get(projection) as Collection<Any>?

        var collection: Collection<Any> = existingCollection ?: when {
            isCollection || isList -> if (isMutable) mutableListOf() else emptyList()
            isSet -> if (isMutable) mutableSetOf() else emptySet()
            else -> error("Unsupported collection type $propertyType")
        }

        val (item, fetchers) = readLogic()

        if (item != null) {
            if (collection is MutableCollection<Any>) {
                try {
                    collection.add(item)
                } catch (_: UnsupportedOperationException) {
                    collection = (collection + item).let {
                        if (isSet) it.toMutableSet() else it.toMutableList()
                    }
                }
            } else {
                collection = (collection + item).let {
                    if (isSet) it.toSet() else it
                }
            }
        }

        if (collection !== existingCollection) {
            propertyInfo.accessor.set(projection, collection)
        }

        fetchers to HydrationMaterial(projection, parentProjection)
    }
}

internal fun readTupleIntoMap(
    propertyInfo: PropertyInfo,
    projection: Any,
    parentProjection: Any?,
    readLogic: () -> Pair<Pair<Any?, Any?>?, List<Fetcher>>
): Pair<List<Fetcher>, HydrationMaterial?> {
    return with(propertyInfo) {
        @Suppress("UNCHECKED_CAST")
        val existingMap = propertyInfo.accessor.get(projection) as Map<Any?, Any?>?

        var map: Map<Any?, Any?> = existingMap ?: when {
            isMap -> if (isMutable) mutableMapOf() else emptyMap()
            else -> error("Unsupported collection type $propertyType")
        }

        val (entry, fetchers) = readLogic()

        if (entry != null) {
            if (map is MutableMap<Any?, Any?>) {
                try {
                    map.put(entry.first, entry.second)
                } catch (_: UnsupportedOperationException) {
                    map = (map + entry).toMutableMap()
                }
            } else {
                map = map + entry
            }
        }

        if (map !== existingMap) {
            propertyInfo.accessor.set(projection, map)
        }

        fetchers to HydrationMaterial(projection, parentProjection)
    }
}

internal fun getProjectionIdProp(projectionClassImpl: KClass<*>, entityClass: KClass<*>): KProperty1<*, *> {
    return entityClass.memberProperties
        .filter { it.annotatedWith<Id>() || it.annotatedWith<EmbeddedId>() }
        .also {
            require(it.size == 1) {
                "Expected 1 ID property but found ${it.size}. Entity class `${entityClass.qualifiedName}`"
            }
        }
        .single()
        .let { entityIdProp ->
            projectionClassImpl.memberProperties.single { it.name == entityIdProp.name }
        }
}
