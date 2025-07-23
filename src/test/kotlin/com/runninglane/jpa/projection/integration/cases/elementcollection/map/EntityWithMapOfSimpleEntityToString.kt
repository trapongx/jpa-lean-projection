package com.runninglane.jpa.projection.integration.cases.elementcollection.map

import javax.persistence.*

@Entity
class EntityWithMapOfSimpleEntityToString {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ElementCollection
    var elements: Map<SimpleEntity, String>? = null
}