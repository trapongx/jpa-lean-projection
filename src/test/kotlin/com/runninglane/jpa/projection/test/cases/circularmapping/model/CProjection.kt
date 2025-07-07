package com.runninglane.jpa.projection.test.cases.circularmapping.model

interface CProjection {
    val id: Long
    val name: String?
    val a: AProjection?
}