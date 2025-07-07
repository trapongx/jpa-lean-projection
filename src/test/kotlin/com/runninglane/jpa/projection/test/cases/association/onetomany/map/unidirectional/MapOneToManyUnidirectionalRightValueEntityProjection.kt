package com.runninglane.jpa.projection.test.cases.association.onetomany.map.unidirectional

class MapOneToManyUnidirectionalRightValueEntityProjection {
    var id: Long = 0L
    var double: Double? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapOneToManyUnidirectionalRightKeyEntityProjection) return false
        return id == other.id
    }
}