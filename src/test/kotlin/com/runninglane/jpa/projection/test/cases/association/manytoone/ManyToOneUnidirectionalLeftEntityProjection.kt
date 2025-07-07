package com.runninglane.jpa.projection.test.cases.association.manytoone

interface ManyToOneUnidirectionalLeftEntityProjection {
    val id: Long
    val right: ManyToOneUnidirectionalRightEntityProjection?
}