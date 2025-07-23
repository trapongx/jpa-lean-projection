package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.bidirectional

interface MapOneToManyBidirectionalLeftEntityWithStringKeyProjection {
    val id: Long
    val string: String?
    val rightMap: Map<String, MapOneToManyBidirectionalRightValueEntityWithStringKey>?
}