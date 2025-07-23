package com.runninglane.jpa.projection.integration.cases.elementcollection.map

interface EntityWithMapOfStringToIntProjection {
    val id: Long
    val elements: Map<String, Int>?
}