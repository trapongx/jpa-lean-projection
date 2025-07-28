package com.runninglane.jpa.projection.integration.cases.association.onetoonetomany

interface MiddleEntityProjection {
    val id: Long
    val name: String?
    val rightList: List<RightEntityProjection>?
}