package com.runninglane.jpa.projection.integration.cases.elementcollection.map

interface EntityWithMapOfComplexEntityToStringProjectionWithProjectedKey {
    val id: Long
    val elements: Map<ComplexEntityProjection, String, >?
}