package com.runninglane.jpa.projection.test.cases.association.manytomany.map.unidirectional

interface MapManyToManyUnidirectionalLeftEntityWithStringKeyProjection {
    val id: Long
    val string: String?
    val rightMap: Map<String, MapManyToManyUnidirectionalRightValueEntity>?
}