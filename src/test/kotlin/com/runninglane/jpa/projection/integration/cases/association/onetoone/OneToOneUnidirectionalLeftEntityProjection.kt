package com.runninglane.jpa.projection.integration.cases.association.onetoone

interface OneToOneUnidirectionalLeftEntityProjection {
    val id: Long
    val right: OneToOneUnidirectionalRightEntityProjection?
}