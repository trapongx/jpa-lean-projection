package com.runninglane.jpa.projection.test.cases.elementcollection.map

interface EntityWithMapOfSimpleEntityToStringProjectionWithProjectedKey {
    val id: Long
    val elements: Map<SimpleEntityProjection, String>?
}