package com.runninglane.jpa.projection.test.cases.association.manytomany.map.unidirectional

interface MapManyToManyUnidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue {
    val id: Long
    val string: String?
    val rightMap: Map<String, MapManyToManyUnidirectionalRightValueEntityProjection>?
}