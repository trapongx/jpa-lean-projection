package com.runninglane.jpa.projection.integration.cases.elementcollection.map

interface EntityWithMapOfComplexEntityToStringProjection {
    val id: Long
    val elements: Map<ComplexEntity, String, >?
}