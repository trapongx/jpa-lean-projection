package com.runninglane.jpa.projection.test.cases.elementcollection.map

interface EntityWithMapOfStringToIntProjection {
    val id: Long
    val elements: Map<String, Int>?
}