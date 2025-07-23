package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.unidirectional

interface MapManyToManyUnidirectionalLeftEntityWithEntityKeyProjection {
    val id: Long
    val string: String?
    val rightMap: Map<MapManyToManyUnidirectionalRightKeyEntity, MapManyToManyUnidirectionalRightValueEntity>?
}