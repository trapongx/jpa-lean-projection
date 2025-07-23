package com.runninglane.jpa.projection.integration.cases.association.onetoone

interface OneToOneBidirectionalRightEntityProjection {
    val id: Long
    val int: Int?
    val string: String?
    val left: OneToOneBidirectionalLeftEntityProjection?
}