package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.unidirectional

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class MapOneToManyUnidirectionalRightValueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var double: Double? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapOneToManyUnidirectionalRightValueEntity) return false
        return id == other.id
    }
}