package com.runninglane.jpa.projection.test.cases.association.manytomany.map.unidirectional

interface MapManyToManyUnidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue {
    val id: Long
    val string: String?
    val rightMap: Map<MapManyToManyUnidirectionalRightKeyEntityProjection, MapManyToManyUnidirectionalRightValueEntityProjection>?
}