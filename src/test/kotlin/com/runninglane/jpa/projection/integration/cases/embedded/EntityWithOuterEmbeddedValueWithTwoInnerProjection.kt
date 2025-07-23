package com.runninglane.jpa.projection.integration.cases.embedded

interface EntityWithOuterEmbeddedValueWithTwoInnerProjection {
    val id: Long
    val embedded: OuterEmbeddableValueWithTwoInner?
}