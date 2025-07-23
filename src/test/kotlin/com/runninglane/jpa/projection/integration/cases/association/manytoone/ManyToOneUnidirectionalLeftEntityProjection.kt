package com.runninglane.jpa.projection.integration.cases.association.manytoone

interface ManyToOneUnidirectionalLeftEntityProjection {
    val id: Long
    val right: ManyToOneUnidirectionalRightEntityProjection?
}