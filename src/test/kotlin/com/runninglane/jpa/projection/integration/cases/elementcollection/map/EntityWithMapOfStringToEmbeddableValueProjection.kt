package com.runninglane.jpa.projection.integration.cases.elementcollection.map

interface EntityWithMapOfStringToEmbeddableValueProjection {
    val id: Long
    val elements: Map<String, EmbeddableValue>?
}