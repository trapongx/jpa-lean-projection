package com.runninglane.jpa.projection.test.cases.association.onetoone

interface OneToOneUnidirectionalLeftEntityProjection {
    val id: Long
    val right: OneToOneUnidirectionalRightEntityProjection?
}