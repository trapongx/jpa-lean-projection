package com.runninglane.jpa.projection.test.cases.entityasvalue

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
open class EntityC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
    open var name: String? = null
}