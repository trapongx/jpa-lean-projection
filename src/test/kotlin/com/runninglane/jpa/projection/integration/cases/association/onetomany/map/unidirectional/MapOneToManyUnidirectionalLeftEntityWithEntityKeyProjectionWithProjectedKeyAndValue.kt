package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.unidirectional

interface MapOneToManyUnidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue {
    val id: Long
    val string: String?
    val rightMap: Map<MapOneToManyUnidirectionalRightKeyEntityProjection, MapOneToManyUnidirectionalRightValueEntityProjection>?
}