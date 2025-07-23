package com.runninglane.jpa.projection.integration.cases.elementcollection.map

import javax.persistence.*

@Entity
class EntityWithMapOfStringToInt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ElementCollection
    @CollectionTable(
        name = "entity_elements_string_to_int",
        joinColumns = [JoinColumn(name = "entity_id")]
    )
    @MapKeyColumn(name = "map_key")
    @Column(name = "map_value")
    var elements: MutableMap<String, Int>? = null
}