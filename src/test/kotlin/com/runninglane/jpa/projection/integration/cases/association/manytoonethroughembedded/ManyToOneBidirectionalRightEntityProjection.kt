package com.runninglane.jpa.projection.integration.cases.association.manytoonethroughembedded
interface ManyToOneBidirectionalRightEntityProjection {
    val id: Long
    val int: Int?
    val string: String?
    val leftList: List<ManyToOneBidirectionalLeftEntityProjection>?
}