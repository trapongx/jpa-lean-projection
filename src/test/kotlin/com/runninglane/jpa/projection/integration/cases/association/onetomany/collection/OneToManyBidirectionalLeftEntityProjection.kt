package com.runninglane.jpa.projection.integration.cases.association.onetomany.collection

interface OneToManyBidirectionalLeftEntityProjection {
    val id: Long
    val rightList: List<OneToManyBidirectionalRightEntityProjection>?
}