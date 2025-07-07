package com.runninglane.jpa.projection.test.cases.extensions

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
class EntityWithEmbeddedId {
    @EmbeddedId
    var embeddableId: EmbeddableAsId? = null
}