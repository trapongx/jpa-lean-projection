package com.runninglane.jpa.projection.test.cases.association.onetomany.collection

interface OneToManyBidirectionalRightEntityProjection {
    val id: Long
    val int: Int?
    val string: String?
    val left: OneToManyBidirectionalLeftEntityProjection?
}