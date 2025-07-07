package com.runninglane.jpa.projection.test.cases.customproperties

interface Projection2 : CustomProjectionSuperInterface2<TestEntity> {
    val id: Long
    val name: String?
}