package com.runninglane.jpa.projection.integration.cases.embedded

interface EntityWithOuterEmbeddedValueWithOneInnerProjectionWithProjectedInsideProjectedEmbedded {
    val id: Long
    val embedded: OuterEmbeddableValueWithOneInnerProjection
}