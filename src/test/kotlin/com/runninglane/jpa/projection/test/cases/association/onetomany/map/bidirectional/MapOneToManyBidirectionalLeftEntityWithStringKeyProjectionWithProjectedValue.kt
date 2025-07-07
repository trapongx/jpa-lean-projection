package com.runninglane.jpa.projection.test.cases.association.onetomany.map.bidirectional

interface MapOneToManyBidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue {
    val id: Long
    val string: String?
    val rightMap: Map<String, MapOneToManyBidirectionalRightValueEntityWithStringKeyProjection>?
}
