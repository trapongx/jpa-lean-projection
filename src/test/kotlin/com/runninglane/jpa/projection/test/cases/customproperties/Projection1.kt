package com.runninglane.jpa.projection.test.cases.customproperties

interface Projection1 : CustomProjectionSuperInterface1<TestEntity> {
    val id: Long
    val name: String?
}