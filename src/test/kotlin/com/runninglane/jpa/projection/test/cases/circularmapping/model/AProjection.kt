package com.runninglane.jpa.projection.test.cases.circularmapping.model

interface AProjection {
    val id: Long
    val name: String?
    val b: BProjection?
}