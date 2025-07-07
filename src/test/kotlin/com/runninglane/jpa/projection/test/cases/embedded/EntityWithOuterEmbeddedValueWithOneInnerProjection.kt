package com.runninglane.jpa.projection.test.cases.embedded

interface EntityWithOuterEmbeddedValueWithOneInnerProjection {
    val id: Long
    val embedded: OuterEmbeddableValueWithOneInner?
}