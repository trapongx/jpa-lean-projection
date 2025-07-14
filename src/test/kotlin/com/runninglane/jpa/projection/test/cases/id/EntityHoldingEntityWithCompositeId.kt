package com.runninglane.jpa.projection.test.cases.id

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@Entity
class EntityHoldingEntityWithCompositeId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @OneToOne
    var single: EntityWithCompositeId? = null
    @OneToMany
    var list: List<EntityWithCompositeId>? = null
}