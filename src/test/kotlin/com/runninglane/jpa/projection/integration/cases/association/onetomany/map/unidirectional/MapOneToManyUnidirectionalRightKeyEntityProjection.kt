package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.unidirectional

class MapOneToManyUnidirectionalRightKeyEntityProjection {
    var id: Long = 0L
    var int: Int? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapOneToManyUnidirectionalRightKeyEntityProjection) return false
        return id == other.id
    }
}