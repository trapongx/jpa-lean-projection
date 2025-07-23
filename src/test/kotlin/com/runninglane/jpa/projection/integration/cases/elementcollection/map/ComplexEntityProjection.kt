package com.runninglane.jpa.projection.integration.cases.elementcollection.map

class ComplexEntityProjection {
    var id: Long = 0L
    var string: String? = null
    var associated: SimpleEntityProjection? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleEntityProjection) return false
        return id == other.id
    }
}