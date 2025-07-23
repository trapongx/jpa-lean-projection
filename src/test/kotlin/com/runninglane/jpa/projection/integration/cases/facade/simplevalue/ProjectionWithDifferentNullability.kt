package com.runninglane.jpa.projection.integration.cases.facade.simplevalue

interface ProjectionWithDifferentNullability {
    val id: Long
    var int: Int?
    var long: Long
}