package com.runninglane.jpa.projection.integration.cases.elementcollection.collection

interface EntityWithListOfEmbeddableValueProjection {
    val id: Long
    val elements: List<EmbeddableValueProjection>
}
