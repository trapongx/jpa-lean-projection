package com.runninglane.jpa.projection.test.cases.elementcollection.map

import javax.persistence.*

@Entity
class EntityWithMapOfEmbeddableValueToDouble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ElementCollection
    var elements: Map<EmbeddableValue, Double>? = null
}