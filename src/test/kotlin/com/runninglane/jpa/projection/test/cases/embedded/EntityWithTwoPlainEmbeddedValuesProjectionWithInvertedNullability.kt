package com.runninglane.jpa.projection.test.cases.embedded

interface EntityWithTwoPlainEmbeddedValuesProjectionWithInvertedNullability {
    val id: Long
    var embedded1: EmbeddableValue?
    var embedded2: EmbeddableValue
}