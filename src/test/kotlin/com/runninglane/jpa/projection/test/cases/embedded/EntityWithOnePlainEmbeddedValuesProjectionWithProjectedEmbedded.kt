package com.runninglane.jpa.projection.test.cases.embedded

interface EntityWithOnePlainEmbeddedValuesProjectionWithProjectedEmbedded {
    val id: Long
    val embedded: EmbeddableValueProjection
}