package com.runninglane.jpa.projection.test.cases.association.onetomany.map.unidirectional

interface MapOneToManyUnidirectionalLeftEntityWithStringKeyProjection {
    val id: Long
    val string: String?
    val rightMap: Map<String, MapOneToManyUnidirectionalRightValueEntity>?
}