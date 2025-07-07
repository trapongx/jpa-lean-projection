package com.runninglane.jpa.projection.test.cases.elementcollection.map

interface EntityWithMapOfEmbeddableValueToEmbeddableValueProjection {
    val id: Long
    val elements: Map<EmbeddableValue, EmbeddableValue>?
}