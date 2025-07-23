package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.entitykey

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class MapManyToManyBidirectionalRightKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var int: Int? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapManyToManyBidirectionalRightKeyEntity) return false
        return id == other.id
    }
}