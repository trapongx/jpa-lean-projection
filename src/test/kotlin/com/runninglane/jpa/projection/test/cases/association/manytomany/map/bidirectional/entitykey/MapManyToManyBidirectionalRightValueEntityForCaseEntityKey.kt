package com.runninglane.jpa.projection.test.cases.association.manytomany.map.bidirectional.entitykey

import javax.persistence.*

@Entity
class MapManyToManyBidirectionalRightValueEntityForCaseEntityKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var double: Double? = null
    @ManyToMany(mappedBy = "rightMap", fetch = FetchType.EAGER)
    var leftSet: Set<MapManyToManyBidirectionalLeftEntityWithEntityKey>? = null

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapManyToManyBidirectionalRightValueEntityForCaseEntityKey) return false
        return id == other.id
    }
}