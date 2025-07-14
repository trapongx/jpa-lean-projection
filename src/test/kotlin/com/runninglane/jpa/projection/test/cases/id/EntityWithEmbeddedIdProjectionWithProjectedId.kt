package com.runninglane.jpa.projection.test.cases.id

interface EntityWithEmbeddedIdProjectionWithProjectedId {
    var id: EmbeddableIdProjection
    var name: String?
}