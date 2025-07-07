package com.runninglane.jpa.projection.test.cases.elementcollection.map

interface EntityWithMapOfEmbeddableValueToDoubleProjection {
    val id: Long
    val elements: Map<EmbeddableValue, Double>?
}