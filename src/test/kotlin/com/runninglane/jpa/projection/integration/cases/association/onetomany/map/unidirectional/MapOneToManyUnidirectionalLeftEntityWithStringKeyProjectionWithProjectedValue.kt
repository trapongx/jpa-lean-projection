package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.unidirectional

interface MapOneToManyUnidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue {
    val id: Long
    val string: String?
    val rightMap: Map<String, MapOneToManyUnidirectionalRightValueEntityProjection>?
}