package com.runninglane.jpa.projection.integration.cases.embedded

interface EntityWithOnePlainEmbeddedValuesProjectionWithProjectedEmbedded {
    val id: Long
    val embedded: EmbeddableValueProjection
}