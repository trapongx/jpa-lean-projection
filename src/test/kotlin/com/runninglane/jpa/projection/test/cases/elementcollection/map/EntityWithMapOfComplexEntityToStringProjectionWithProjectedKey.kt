package com.runninglane.jpa.projection.test.cases.elementcollection.map

interface EntityWithMapOfComplexEntityToStringProjectionWithProjectedKey {
    val id: Long
    val elements: Map<ComplexEntityProjection, String, >?
}