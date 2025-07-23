package com.runninglane.jpa.projection.integration.cases.entityasvalue

import javax.persistence.*

@Entity
open class EntityB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
    @OneToOne
    open var c: EntityC? = null
    @OneToMany
    open var dList: MutableList<EntityD>? = null
    @OneToMany
    open var eList: MutableList<EntityE>? = null
    @ElementCollection(targetClass = EntityD::class)
    open var dCollection: List<EntityD>? = null
    @ElementCollection
    open var eToString: Map<EntityE, String>? = null
}