package com.runninglane.jpa.projection.test.cases.elementcollection.collection

interface EntityWithTwoListOfStringProjection {
    val id: Long
    val elements1: List<String>?
    val elements2: List<String>?
}