package com.runninglane.jpa.projection.integration.cases.elementcollection.map

interface EntityWithMapOfSimpleEntityToStringProjection {
    val id: Long
    val elements: Map<SimpleEntity, String>?
}