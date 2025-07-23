package com.runninglane.jpa.projection.integration.cases.elementcollection.map

import javax.persistence.*

@Entity
class EntityWithMapOfComplexEntityToString {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ElementCollection
    var elements: Map<ComplexEntity, String>? = null
}