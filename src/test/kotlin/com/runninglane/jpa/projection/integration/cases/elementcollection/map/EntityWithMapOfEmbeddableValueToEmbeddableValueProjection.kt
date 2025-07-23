package com.runninglane.jpa.projection.integration.cases.elementcollection.map

interface EntityWithMapOfEmbeddableValueToEmbeddableValueProjection {
    val id: Long
    val elements: Map<EmbeddableValue, EmbeddableValue>?
}