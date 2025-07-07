package com.runninglane.jpa.projection.test.cases.equality

import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@Entity
class EntityWithEmbeddedId {
    @EmbeddedId
    var id: EmbeddableId? = null
    var name: String? = null
}