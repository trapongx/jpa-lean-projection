package com.runninglane.jpa.projection.test.cases.association.onetomany.collection

interface OneToManyBidirectionalLeftEntityProjection {
    val id: Long
    val rightList: List<OneToManyBidirectionalRightEntityProjection>?
}