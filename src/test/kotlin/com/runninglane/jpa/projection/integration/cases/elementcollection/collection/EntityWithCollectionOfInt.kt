package com.runninglane.jpa.projection.integration.cases.elementcollection.collection

import javax.persistence.*

@Entity
class EntityWithCollectionOfInt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ElementCollection
    var elements: Collection<Int>? = null
}