package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.bidirectional

import javax.persistence.*

@Entity
class MapOneToManyBidirectionalRightValueEntityWithEntityKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var double: Double? = null
    @ManyToOne
    var entityKey: MapOneToManyBidirectionalRightKeyEntity? = null
    @ManyToOne
    var left: MapOneToManyBidirectionalLeftEntityWithEntityKey? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapOneToManyBidirectionalRightValueEntityWithEntityKey) return false
        return id == other.id
    }
}