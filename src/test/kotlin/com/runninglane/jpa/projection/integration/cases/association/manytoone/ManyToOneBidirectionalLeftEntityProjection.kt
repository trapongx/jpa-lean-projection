package com.runninglane.jpa.projection.integration.cases.association.manytoone

interface ManyToOneBidirectionalLeftEntityProjection {
    val id: Long
    val right: ManyToOneBidirectionalRightProjection?
}