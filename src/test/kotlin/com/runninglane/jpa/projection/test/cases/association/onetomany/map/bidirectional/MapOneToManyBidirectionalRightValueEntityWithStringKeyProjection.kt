package com.runninglane.jpa.projection.test.cases.association.onetomany.map.bidirectional

class MapOneToManyBidirectionalRightValueEntityWithStringKeyProjection {
    var id: Long = 0L
    var double: Double? = null
    var stringKey: String? = null
    var left: MapOneToManyBidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapOneToManyBidirectionalRightKeyEntityProjection) return false
        return id == other.id
    }
}