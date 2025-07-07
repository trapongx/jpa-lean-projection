package com.runninglane.jpa.projection.test.cases.elementcollection.map

interface EntityWithMapOfStringToEmbeddableValueProjection {
    val id: Long
    val elements: Map<String, EmbeddableValue>?
}