package com.runninglane.jpa.projection.test.cases.association.manytomany.map.bidirectional.entitykey

interface MapManyToManyBidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue {
    val id: Long
    val string: String?
    val rightMap: Map<MapManyToManyBidirectionalRightKeyEntityProjection, MapManyToManyBidirectionalRightValueEntityForCaseEntityKeyProjectionWithProjectedKeyAndValue>?
}