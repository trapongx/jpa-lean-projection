package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.unidirectional

import javax.persistence.*

@Entity
class MapManyToManyUnidirectionalLeftEntityWithStringKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var string: String? = null
    @ManyToMany
    var rightMap: Map<String, MapManyToManyUnidirectionalRightValueEntity>? = null
}