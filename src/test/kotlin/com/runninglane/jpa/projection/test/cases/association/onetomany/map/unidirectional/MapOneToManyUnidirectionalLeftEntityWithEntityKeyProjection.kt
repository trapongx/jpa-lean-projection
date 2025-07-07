package com.runninglane.jpa.projection.test.cases.association.onetomany.map.unidirectional

interface MapOneToManyUnidirectionalLeftEntityWithEntityKeyProjection {
    val id: Long
    val string: String?
    val rightMap: Map<MapOneToManyUnidirectionalRightKeyEntity, MapOneToManyUnidirectionalRightValueEntity>?
}