package com.runninglane.jpa.projection.integration.cases.extensions

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
class EntityWithEmbeddedId {
    @EmbeddedId
    var embeddableId: EmbeddableAsId? = null
}