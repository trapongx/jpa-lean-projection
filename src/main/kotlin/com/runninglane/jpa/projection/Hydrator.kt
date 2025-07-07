package com.runninglane.jpa.projection

import com.runninglane.jpa.projection.mapper.EntityClassMapper
import com.runninglane.jpa.projection.mapper.Fetcher
import com.runninglane.jpa.projection.mapper.Mapper
import com.runninglane.jpa.projection.mapper.sametype.SameTypePropertyMapper
import javax.persistence.Tuple

internal class Hydrator private constructor(val mapper: EntityClassMapper) {
    init {
        require(mapper.hasJoinFetch()) { "Hydrator requires a mapper with join fetch" }
    }

    val mapperUsingJoinFetch: Mapper =
        generateSequence(mapper.getChildren().find { it.hasJoinFetch() }!!) { mapper ->
            mapper.getChildren().find { it.hasJoinFetch() }
        }.last()

    val parentIdMappers = generateSequence(mapperUsingJoinFetch.getParent()!!) { it.getParent() }
        .flatMap { parent ->
            parent.getChildren()
                .filterIsInstance<SameTypePropertyMapper>()
                .filter { it.isIdProperty }
        }
        .sortedBy { it.tupleIndex }
        .toList()

    fun newHydration() = Stateful()

    inner class Stateful {
        private var currentParentIds: Map<Int, Any?>? = null

        private fun readParentIds(tuple: Tuple): Map<Int, Any?> {
            return parentIdMappers.associate { idMapper ->
                val idValue = tuple.get(idMapper.tupleIndex)
                idMapper.tupleIndex to idValue
            }
        }

        fun hydrate(
            tuple: Tuple,
            hydrationMaterialFromPreviousTuple: HydrationMaterial?,
            projectionIdentityMap: ProjectionIdentityMap
        ): Pair<Boolean, List<Fetcher>> {
            val parentIds = readParentIds(tuple)
            if (parentIds == currentParentIds) {
                val (fetchers, _) = mapperUsingJoinFetch.readTuple(
                    tuple,
                    hydrationMaterialFromPreviousTuple!!.projection,
                    hydrationMaterialFromPreviousTuple.parentProjection,
                    projectionIdentityMap
                )
                return Pair(true, fetchers)
            } else {
                currentParentIds = parentIds
                return Pair(false, emptyList())
            }
        }
    }

    companion object {
        private val cache = mutableMapOf<EntityClassMapper, Hydrator>()

        fun create(mapper: EntityClassMapper): Hydrator {
            return cache[mapper] ?: Hydrator(mapper).also { cache[mapper] = it }
        }
    }
}