package com.runninglane.jpa.projection.integration.cases.entityasvalue

import javax.persistence.*

@Entity
open class EntityA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
    @OneToOne
    open var b: EntityB? = null
}