package com.runninglane.jpa.projection.test.cases.elementcollection.collection

import javax.persistence.*

@Entity
class EntityWithCollectionOfInt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ElementCollection
    var elements: Collection<Int>? = null
}