package com.runninglane.jpa.projection.test.cases.elementcollection.collection

interface EntityWithListOfStringProjectionWithInvertedNullabilityAndMutability {
    val id: Long
    val elements: MutableList<String>
}