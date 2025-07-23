package com.runninglane.jpa.projection.integration.cases.association.manytoone

interface ManyToOneBidirectionalRightProjection {
    val id: Long
    val int: Int?
    val string: String?
    val leftList: List<ManyToOneBidirectionalLeftEntityProjection>?
}