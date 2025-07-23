package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.stringkey

import com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.entitykey.MapManyToManyBidirectionalRightKeyEntityProjection

class MapManyToManyBidirectionalRightValueEntityForCaseStringKeyProjection {
    var id: Long = 0L
    var double: Double? = null
    var leftSet: Set<MapManyToManyBidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue>? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapManyToManyBidirectionalRightKeyEntityProjection) return false
        return id == other.id
    }
}