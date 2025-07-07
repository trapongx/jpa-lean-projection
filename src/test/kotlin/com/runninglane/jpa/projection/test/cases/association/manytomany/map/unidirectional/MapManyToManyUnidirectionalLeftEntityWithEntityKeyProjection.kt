package com.runninglane.jpa.projection.test.cases.association.manytomany.map.unidirectional

interface MapManyToManyUnidirectionalLeftEntityWithEntityKeyProjection {
    val id: Long
    val string: String?
    val rightMap: Map<MapManyToManyUnidirectionalRightKeyEntity, MapManyToManyUnidirectionalRightValueEntity>?
}