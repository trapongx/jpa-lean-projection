package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.stringkey

import javax.persistence.*

@Entity
class MapManyToManyBidirectionalRightValueEntityForCaseStringKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var double: Double? = null
    @ManyToMany(mappedBy = "rightMap", fetch = FetchType.EAGER)
    var leftSet: Set<MapManyToManyBidirectionalLeftEntityWithStringKey>? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapManyToManyBidirectionalRightValueEntityForCaseStringKey) return false
        return id == other.id
    }
}