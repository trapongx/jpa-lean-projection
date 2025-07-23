package com.runninglane.jpa.projection.integration.cases.elementcollection.collection

import javax.persistence.*

@Entity
class EntityWithTwoListOfString {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ElementCollection
    var elements1: List<String>? = null
    @ElementCollection
    var elements2: List<String>? = null
}