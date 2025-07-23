package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.stringkey

interface MapManyToManyBidirectionalLeftEntityWithStringKeyProjection {
    val id: Long
    val string: String?
    val rightMap: Map<String, MapManyToManyBidirectionalRightValueEntityForCaseStringKey>?
}