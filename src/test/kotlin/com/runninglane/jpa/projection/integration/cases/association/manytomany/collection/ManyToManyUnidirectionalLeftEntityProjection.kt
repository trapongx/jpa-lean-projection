package com.runninglane.jpa.projection.integration.cases.association.manytomany.collection

interface ManyToManyUnidirectionalLeftEntityProjection {
    val id: Long
    val rightList: List<ManyToManyUnidirectionalRightEntityProjection>?
}