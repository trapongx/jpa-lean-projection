package com.runninglane.jpa.projection.test.cases.embedded

interface EntityWithOuterEmbeddedValueWithTwoInnerProjection {
    val id: Long
    val embedded: OuterEmbeddableValueWithTwoInner?
}