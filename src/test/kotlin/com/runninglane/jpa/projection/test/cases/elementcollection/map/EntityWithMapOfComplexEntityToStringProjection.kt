package com.runninglane.jpa.projection.test.cases.elementcollection.map

interface EntityWithMapOfComplexEntityToStringProjection {
    val id: Long
    val elements: Map<ComplexEntity, String, >?
}