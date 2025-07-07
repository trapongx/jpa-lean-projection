package com.runninglane.jpa.projection.test.cases.association.manytomany.collection

interface ManyToManyBidirectionalRightEntityProjection {
    val id: Long
    val int: Int?
    val string: String?
    val leftList: List<ManyToManyBidirectionalLeftEntityProjection>?
}