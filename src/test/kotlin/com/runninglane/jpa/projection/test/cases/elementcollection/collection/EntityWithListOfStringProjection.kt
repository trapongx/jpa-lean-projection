package com.runninglane.jpa.projection.test.cases.elementcollection.collection

interface EntityWithListOfStringProjection {
    val id: Long
    val elements: List<String>?
}