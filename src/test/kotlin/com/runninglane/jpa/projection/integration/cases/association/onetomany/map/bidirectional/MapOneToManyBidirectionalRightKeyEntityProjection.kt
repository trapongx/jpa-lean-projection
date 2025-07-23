package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.bidirectional

class MapOneToManyBidirectionalRightKeyEntityProjection {
    var id: Long = 0L
    var int: Int? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapOneToManyBidirectionalRightKeyEntityProjection) return false
        return id == other.id
    }
}