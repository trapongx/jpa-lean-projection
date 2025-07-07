package com.runninglane.jpa.projection.test.cases.circularmapping.model

interface BProjection {
    val id: Long
    val name: String?
    val c: CProjection?
}