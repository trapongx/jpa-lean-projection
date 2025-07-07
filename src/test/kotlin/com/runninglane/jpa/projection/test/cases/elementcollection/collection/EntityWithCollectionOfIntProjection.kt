package com.runninglane.jpa.projection.test.cases.elementcollection.collection

interface EntityWithCollectionOfIntProjection {
    val id: Long
    val elements: Collection<Int>?
}