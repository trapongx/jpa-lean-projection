package com.runninglane.jpa.projection.integration.cases.elementcollection.map

interface EntityWithMapOfSimpleEntityToStringProjectionWithProjectedKey {
    val id: Long
    val elements: Map<SimpleEntityProjection, String>?
}