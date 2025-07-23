package com.runninglane.jpa.projection.integration.cases.id

interface EntityWithEmbeddedIdProjection {
    var id: EmbeddableId
    var name: String?
}