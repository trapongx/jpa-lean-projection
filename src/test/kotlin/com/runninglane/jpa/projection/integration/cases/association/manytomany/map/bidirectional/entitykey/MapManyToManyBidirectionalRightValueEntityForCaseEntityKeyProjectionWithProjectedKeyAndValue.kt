package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.entitykey

class MapManyToManyBidirectionalRightValueEntityForCaseEntityKeyProjectionWithProjectedKeyAndValue {
    var id: Long = 0L
    var double: Double? = null
    var leftSet: Set<MapManyToManyBidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue>? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapManyToManyBidirectionalRightValueEntityForCaseEntityKeyProjectionWithProjectedKeyAndValue) return false
        return id == other.id
    }
}