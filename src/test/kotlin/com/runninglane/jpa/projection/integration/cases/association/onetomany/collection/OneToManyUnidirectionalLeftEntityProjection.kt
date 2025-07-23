package com.runninglane.jpa.projection.integration.cases.association.onetomany.collection

interface OneToManyUnidirectionalLeftEntityProjection {
    val id: Long
    val rightList: List<OneToManyUnidirectionalRightEntityProjection>?
}