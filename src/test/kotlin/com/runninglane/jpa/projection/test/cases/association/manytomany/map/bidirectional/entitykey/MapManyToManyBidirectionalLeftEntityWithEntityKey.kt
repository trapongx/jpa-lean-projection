package com.runninglane.jpa.projection.test.cases.association.manytomany.map.bidirectional.entitykey

import javax.persistence.*

@Entity
class MapManyToManyBidirectionalLeftEntityWithEntityKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var string: String? = null
    @ManyToMany
    var rightMap: Map<MapManyToManyBidirectionalRightKeyEntity, MapManyToManyBidirectionalRightValueEntityForCaseEntityKey>? = null
}