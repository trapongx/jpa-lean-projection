package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.bidirectional

interface MapOneToManyBidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue {
    val id: Long
    val string: String?
    val rightMap: Map<MapOneToManyBidirectionalRightKeyEntityProjection, MapOneToManyBidirectionalRightValueEntityWithEntityKeyProjection>?
}