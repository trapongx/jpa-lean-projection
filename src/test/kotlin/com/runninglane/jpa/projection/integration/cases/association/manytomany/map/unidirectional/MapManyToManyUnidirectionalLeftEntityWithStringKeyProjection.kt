package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.unidirectional

interface MapManyToManyUnidirectionalLeftEntityWithStringKeyProjection {
    val id: Long
    val string: String?
    val rightMap: Map<String, MapManyToManyUnidirectionalRightValueEntity>?
}