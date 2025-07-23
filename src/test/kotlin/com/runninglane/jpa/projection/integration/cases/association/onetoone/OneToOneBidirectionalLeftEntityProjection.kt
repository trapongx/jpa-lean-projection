package com.runninglane.jpa.projection.integration.cases.association.onetoone

interface OneToOneBidirectionalLeftEntityProjection {
    val id: Long
    val right: OneToOneBidirectionalRightEntityProjection?
}