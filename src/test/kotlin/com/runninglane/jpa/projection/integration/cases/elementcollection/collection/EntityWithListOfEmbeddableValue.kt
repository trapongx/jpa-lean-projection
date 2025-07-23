package com.runninglane.jpa.projection.integration.cases.elementcollection.collection

import javax.persistence.*

@Entity
class EntityWithListOfEmbeddableValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ElementCollection
    var elements: List<EmbeddableValue> = emptyList()
}
