package com.runninglane.jpa.projection.integration.cases.embedded

interface EntityWithOnePlainEmbeddedValuesProjection {
    val id: Long
    val embedded: EmbeddableValue
}