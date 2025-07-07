package com.runninglane.jpa.projection.test.cases.association.manytomany.collection

interface ManyToManyUnidirectionalLeftEntityProjection {
    val id: Long
    val rightList: List<ManyToManyUnidirectionalRightEntityProjection>?
}