package com.runninglane.jpa.projection.test.cases.elementcollection.collection

import javax.persistence.*

@Entity
class EntityWithListOfString {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ElementCollection
    var elements: List<String>? = null
}