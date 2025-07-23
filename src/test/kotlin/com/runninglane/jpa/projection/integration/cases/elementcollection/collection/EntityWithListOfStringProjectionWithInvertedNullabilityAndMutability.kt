package com.runninglane.jpa.projection.integration.cases.elementcollection.collection

interface EntityWithListOfStringProjectionWithInvertedNullabilityAndMutability {
    val id: Long
    val elements: MutableList<String>
}