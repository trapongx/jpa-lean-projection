package com.runninglane.jpa.projection.integration.cases.elementcollection.collection

interface EntityWithListOfStringProjection {
    val id: Long
    val elements: List<String>?
}