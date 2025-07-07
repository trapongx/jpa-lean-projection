package com.runninglane.jpa.projection.test.cases.association.manytomany.map.bidirectional.entitykey

class MapManyToManyBidirectionalRightKeyEntityProjection {
    var id: Long = 0L
    var int: Int? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapManyToManyBidirectionalRightKeyEntityProjection) return false
        return id == other.id
    }
}