package com.runninglane.jpa.projection.integration.cases.elementcollection.collection

interface EntityWithCollectionOfIntProjection {
    val id: Long
    val elements: Collection<Int>?
}