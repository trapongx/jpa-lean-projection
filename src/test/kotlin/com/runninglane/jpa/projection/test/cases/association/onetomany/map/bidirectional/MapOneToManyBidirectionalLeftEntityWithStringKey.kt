package com.runninglane.jpa.projection.test.cases.association.onetomany.map.bidirectional

import javax.persistence.*

@Entity
class MapOneToManyBidirectionalLeftEntityWithStringKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var string: String? = null
    @OneToMany(mappedBy = "left", fetch = FetchType.EAGER)
    @MapKey(name = "stringKey")
    var rightMap: Map<String, MapOneToManyBidirectionalRightValueEntityWithStringKey>? = null
}