package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.bidirectional

import javax.persistence.*

@Entity
class MapOneToManyBidirectionalLeftEntityWithEntityKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var string: String? = null
    @OneToMany(mappedBy = "left", fetch = FetchType.EAGER)
    @MapKeyJoinColumn(name = "entity_key_id")
    var rightMap: Map<MapOneToManyBidirectionalRightKeyEntity, MapOneToManyBidirectionalRightValueEntityWithEntityKey>? = null
}