package com.runninglane.jpa.projection.test.cases.association.onetomany.map.unidirectional

import javax.persistence.*

@Entity
class MapOneToManyUnidirectionalLeftEntityWithEntityKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var string: String? = null
    @OneToMany
    var rightMap: Map<MapOneToManyUnidirectionalRightKeyEntity, MapOneToManyUnidirectionalRightValueEntity>? = null
}