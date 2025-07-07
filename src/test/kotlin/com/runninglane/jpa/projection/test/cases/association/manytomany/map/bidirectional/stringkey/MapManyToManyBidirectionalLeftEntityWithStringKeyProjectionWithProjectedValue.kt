package com.runninglane.jpa.projection.test.cases.association.manytomany.map.bidirectional.stringkey

interface MapManyToManyBidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue {
    val id: Long
    val string: String?
    val rightMap: Map<String, MapManyToManyBidirectionalRightValueEntityForCaseStringKeyProjection>?
}