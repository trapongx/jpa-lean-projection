package com.runninglane.jpa.projection.integration.cases.association.manytomany.collection


interface ManyToManyBidirectionalLeftEntityProjection {
    val id: Long
    val rightList: List<ManyToManyBidirectionalRightEntityProjection>?
}