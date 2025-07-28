package com.runninglane.jpa.projection.integration.cases.association.onetoonetomany

interface LeftEntityProjection {
    val id: Long
    val name: String?
    val middle: MiddleEntityProjection?
}