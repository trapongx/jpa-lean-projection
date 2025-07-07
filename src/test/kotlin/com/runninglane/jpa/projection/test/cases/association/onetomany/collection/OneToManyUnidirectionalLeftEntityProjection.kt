package com.runninglane.jpa.projection.test.cases.association.onetomany.collection

interface OneToManyUnidirectionalLeftEntityProjection {
    val id: Long
    val rightList: List<OneToManyUnidirectionalRightEntityProjection>?
}