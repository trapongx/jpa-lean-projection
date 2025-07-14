package com.runninglane.jpa.projection.test.cases.id

import javax.persistence.*

@Entity
class EntityWithSingleId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
}