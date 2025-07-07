package com.runninglane.jpa.projection.test.cases.equality

interface EntityWithEmbeddedIdProjectionWithProjectedId {
    var id: EmbeddableIdProjection
    var name: String?
}