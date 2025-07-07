package com.runninglane.jpa.projection.test.cases.embedded

interface EntityWithOuterEmbeddedValueWithOneInnerProjectionWithProjectedEmbedded {
    val id: Long
    val embedded: OuterEmbeddableValueWithOneInnerProjection
}