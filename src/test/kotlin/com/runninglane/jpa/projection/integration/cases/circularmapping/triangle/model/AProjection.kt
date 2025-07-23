package com.runninglane.jpa.projection.integration.cases.circularmapping.triangle.model

interface AProjection {
    val id: Long
    val name: String?
    val b: BProjection?
}