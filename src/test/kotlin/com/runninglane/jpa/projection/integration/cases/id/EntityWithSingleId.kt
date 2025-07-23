package com.runninglane.jpa.projection.integration.cases.id

import javax.persistence.*

@Entity
class EntityWithSingleId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
}