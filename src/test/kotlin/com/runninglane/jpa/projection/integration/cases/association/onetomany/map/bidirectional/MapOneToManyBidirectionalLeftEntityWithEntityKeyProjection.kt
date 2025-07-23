package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.bidirectional

interface MapOneToManyBidirectionalLeftEntityWithEntityKeyProjection {
    val id: Long
    val string: String?
    val rightMap: Map<MapOneToManyBidirectionalRightKeyEntity, MapOneToManyBidirectionalRightValueEntityWithEntityKey>?
}