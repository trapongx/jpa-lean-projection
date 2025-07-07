package com.runninglane.jpa.projection.test.cases.equality

interface EntityWithEmbeddedIdProjection {
    var id: EmbeddableId
    var name: String?
}