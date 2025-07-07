package com.runninglane.jpa.projection.test.cases.association.manytoone

interface ManyToOneBidirectionalLeftEntityProjection {
    val id: Long
    val right: ManyToOneBidirectionalRightProjection?
}