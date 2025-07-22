package com.runninglane.jpa.projection.test.cases.entityasvalue

interface ProjectionB {
    val id: Long
    val c: EntityC?
    val dList: List<EntityD>?
    val eList: List<EntityE>?
    val dCollection: List<EntityD>?
    val eToString: Map<EntityE, String>?
}
