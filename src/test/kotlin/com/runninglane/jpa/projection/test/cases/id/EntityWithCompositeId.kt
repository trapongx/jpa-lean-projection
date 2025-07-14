package com.runninglane.jpa.projection.test.cases.id

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass

@Entity
@IdClass(EmbeddableId::class)
class EntityWithCompositeId {
    @Id
    var id1: Long? = null
    @Id
    var id2: Long? = null
    var name: String? = null
}