package com.runninglane.jpa.projection.test.cases.embedded

interface EntityWithOnePlainEmbeddedValuesProjection {
    val id: Long
    val embedded: EmbeddableValue
}