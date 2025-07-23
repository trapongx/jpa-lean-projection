package com.runninglane.jpa.projection.integration.cases.association.onetomany.collection

interface OneToManyBidirectionalRightEntityProjection {
    val id: Long
    val int: Int?
    val string: String?
    val left: OneToManyBidirectionalLeftEntityProjection?
}