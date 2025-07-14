package com.runninglane.jpa.projection.test.cases.id

interface EntityWithEmbeddedIdProjection {
    var id: EmbeddableId
    var name: String?
}