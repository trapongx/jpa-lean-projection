package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.bidirectional

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class MapOneToManyBidirectionalRightKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var int: Int? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapOneToManyBidirectionalRightKeyEntity) return false
        return id == other.id
    }
}