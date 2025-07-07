package com.runninglane.jpa.projection.test.cases.elementcollection.collection

interface EntityWithListOfEmbeddableValueProjection {
    val id: Long
    val elements: List<EmbeddableValueProjection>
}
