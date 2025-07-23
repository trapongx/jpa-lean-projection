package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.entitykey

interface MapManyToManyBidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue {
    val id: Long
    val string: String?
    val rightMap: Map<MapManyToManyBidirectionalRightKeyEntityProjection, MapManyToManyBidirectionalRightValueEntityForCaseEntityKeyProjectionWithProjectedKeyAndValue>?
}