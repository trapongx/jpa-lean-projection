package com.runninglane.jpa.projection.test.cases.embedded

interface EntityWithOuterEmbeddedValueWithOneInnerProjectionWithProjectedInsideProjectedEmbedded {
    val id: Long
    val embedded: OuterEmbeddableValueWithOneInnerProjection
}