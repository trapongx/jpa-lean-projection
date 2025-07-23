package com.runninglane.jpa.projection.integration.cases.circularmapping.triangle.model

interface BProjection {
    val id: Long
    val name: String?
    val c: CProjection?
}