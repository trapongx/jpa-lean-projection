package com.runninglane.jpa.projection.test.cases.elementcollection.map

interface EntityWithMapOfSimpleEntityToStringProjection {
    val id: Long
    val elements: Map<SimpleEntity, String>?
}