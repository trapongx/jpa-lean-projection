package com.runninglane.jpa.projection.test.cases.elementcollection.collection

import javax.persistence.*

@Entity
class EntityWithListOfEmbeddableValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ElementCollection
    var elements: List<EmbeddableValue> = emptyList()
}
