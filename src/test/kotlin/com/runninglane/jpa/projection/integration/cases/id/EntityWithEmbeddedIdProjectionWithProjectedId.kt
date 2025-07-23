package com.runninglane.jpa.projection.integration.cases.id

interface EntityWithEmbeddedIdProjectionWithProjectedId {
    var id: EmbeddableIdProjection
    var name: String?
}