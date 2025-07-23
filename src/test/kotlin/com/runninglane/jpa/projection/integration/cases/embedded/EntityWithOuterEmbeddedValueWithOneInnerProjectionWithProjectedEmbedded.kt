package com.runninglane.jpa.projection.integration.cases.embedded

interface EntityWithOuterEmbeddedValueWithOneInnerProjectionWithProjectedEmbedded {
    val id: Long
    val embedded: OuterEmbeddableValueWithOneInnerProjection
}