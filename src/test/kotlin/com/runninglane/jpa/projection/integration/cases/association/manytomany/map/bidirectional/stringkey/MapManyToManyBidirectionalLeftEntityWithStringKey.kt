package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.stringkey

import javax.persistence.*

@Entity
class MapManyToManyBidirectionalLeftEntityWithStringKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var string: String? = null
    @ManyToMany
    var rightMap: Map<String, MapManyToManyBidirectionalRightValueEntityForCaseStringKey>? = null
}