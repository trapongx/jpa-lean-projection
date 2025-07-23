package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.unidirectional

class MapManyToManyUnidirectionalRightKeyEntityProjection {
    var id: Long = 0L
    var int: Int? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapManyToManyUnidirectionalRightKeyEntityProjection) return false
        return id == other.id
    }
}