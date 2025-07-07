package com.runninglane.jpa.projection.test.cases.association.onetomany.map.bidirectional

interface MapOneToManyBidirectionalLeftEntityWithEntityKeyProjection {
    val id: Long
    val string: String?
    val rightMap: Map<MapOneToManyBidirectionalRightKeyEntity, MapOneToManyBidirectionalRightValueEntityWithEntityKey>?
}