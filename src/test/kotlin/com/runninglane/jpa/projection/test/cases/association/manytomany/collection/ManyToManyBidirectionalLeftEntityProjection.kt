package com.runninglane.jpa.projection.test.cases.association.manytomany.collection


interface ManyToManyBidirectionalLeftEntityProjection {
    val id: Long
    val rightList: List<ManyToManyBidirectionalRightEntityProjection>?
}