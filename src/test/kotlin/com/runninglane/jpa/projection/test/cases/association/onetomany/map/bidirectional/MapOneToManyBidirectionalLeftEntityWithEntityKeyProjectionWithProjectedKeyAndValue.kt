package com.runninglane.jpa.projection.test.cases.association.onetomany.map.bidirectional

interface MapOneToManyBidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue {
    val id: Long
    val string: String?
    val rightMap: Map<MapOneToManyBidirectionalRightKeyEntityProjection, MapOneToManyBidirectionalRightValueEntityWithEntityKeyProjection>?
}