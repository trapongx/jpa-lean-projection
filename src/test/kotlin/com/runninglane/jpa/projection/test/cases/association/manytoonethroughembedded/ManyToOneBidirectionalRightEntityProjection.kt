package com.runninglane.jpa.projection.test.cases.association.manytoonethroughembedded
interface ManyToOneBidirectionalRightEntityProjection {
    val id: Long
    val int: Int?
    val string: String?
    val leftList: List<ManyToOneBidirectionalLeftEntityProjection>?
}