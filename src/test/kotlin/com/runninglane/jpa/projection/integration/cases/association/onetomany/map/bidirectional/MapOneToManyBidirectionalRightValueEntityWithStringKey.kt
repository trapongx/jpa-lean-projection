package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.bidirectional

import javax.persistence.*

@Entity
class MapOneToManyBidirectionalRightValueEntityWithStringKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var double: Double? = null
    var stringKey: String? = null
    @ManyToOne
    var left: MapOneToManyBidirectionalLeftEntityWithStringKey? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapOneToManyBidirectionalRightValueEntityWithStringKey) return false
        return id == other.id
    }
}