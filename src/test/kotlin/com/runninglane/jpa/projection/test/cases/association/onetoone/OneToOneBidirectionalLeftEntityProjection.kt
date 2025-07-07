package com.runninglane.jpa.projection.test.cases.association.onetoone

interface OneToOneBidirectionalLeftEntityProjection {
    val id: Long
    val right: OneToOneBidirectionalRightEntityProjection?
}