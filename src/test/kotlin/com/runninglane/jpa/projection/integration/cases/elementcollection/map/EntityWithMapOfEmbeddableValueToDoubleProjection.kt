package com.runninglane.jpa.projection.integration.cases.elementcollection.map

interface EntityWithMapOfEmbeddableValueToDoubleProjection {
    val id: Long
    val elements: Map<EmbeddableValue, Double>?
}