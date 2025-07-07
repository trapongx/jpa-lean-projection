package com.runninglane.jpa.projection.test.cases.embedded

interface EntityWithTwoPlainEmbeddedValuesProjection {
    val id: Long
    val embedded1: EmbeddableValue
    val embedded2: EmbeddableValue?
}