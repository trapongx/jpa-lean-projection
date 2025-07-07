package com.runninglane.jpa.projection.test.cases.association.manytomany.map.bidirectional.entitykey

interface MapManyToManyBidirectionalLeftEntityWithEntityKeyProjection {
    val id: Long
    val string: String?
    val rightMap: Map<MapManyToManyBidirectionalRightKeyEntity, MapManyToManyBidirectionalRightValueEntityForCaseEntityKey>?
}