package com.runninglane.jpa.projection.integration.cases.entityasvalue

interface ProjectionA {
    val id: Long
    val b: ProjectionB?
}