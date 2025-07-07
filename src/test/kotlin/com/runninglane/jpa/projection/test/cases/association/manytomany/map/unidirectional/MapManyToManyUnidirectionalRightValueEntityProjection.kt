package com.runninglane.jpa.projection.test.cases.association.manytomany.map.unidirectional

class MapManyToManyUnidirectionalRightValueEntityProjection {
    var id: Long = 0L
    var double: Double? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapManyToManyUnidirectionalRightKeyEntityProjection) return false
        return id == other.id
    }
}