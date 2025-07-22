package com.runninglane.jpa.projection.test.cases.entityasvalue

interface ProjectionA {
    val id: Long
    val b: ProjectionB?
}