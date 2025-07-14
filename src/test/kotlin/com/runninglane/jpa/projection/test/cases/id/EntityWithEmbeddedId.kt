package com.runninglane.jpa.projection.test.cases.id

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
class EntityWithEmbeddedId {
    @EmbeddedId
    var id: EmbeddableId? = null
    var name: String? = null
}