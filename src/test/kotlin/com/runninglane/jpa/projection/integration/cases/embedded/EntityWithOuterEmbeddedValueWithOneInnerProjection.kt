package com.runninglane.jpa.projection.integration.cases.embedded

interface EntityWithOuterEmbeddedValueWithOneInnerProjection {
    val id: Long
    val embedded: OuterEmbeddableValueWithOneInner?
}