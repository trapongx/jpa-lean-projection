package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.unidirectional

interface MapManyToManyUnidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue {
    val id: Long
    val string: String?
    val rightMap: Map<MapManyToManyUnidirectionalRightKeyEntityProjection, MapManyToManyUnidirectionalRightValueEntityProjection>?
}