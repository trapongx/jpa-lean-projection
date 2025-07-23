package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.entitykey

interface MapManyToManyBidirectionalLeftEntityWithEntityKeyProjection {
    val id: Long
    val string: String?
    val rightMap: Map<MapManyToManyBidirectionalRightKeyEntity, MapManyToManyBidirectionalRightValueEntityForCaseEntityKey>?
}