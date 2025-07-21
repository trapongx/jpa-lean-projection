package com.runninglane.jpa.projection.test.cases.circularmapping.triangle.model

interface CProjection {
    val id: Long
    val name: String?
    val a: AProjection?
}