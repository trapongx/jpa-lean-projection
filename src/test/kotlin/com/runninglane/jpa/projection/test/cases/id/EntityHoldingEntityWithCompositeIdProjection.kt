package com.runninglane.jpa.projection.test.cases.id

interface EntityHoldingEntityWithCompositeIdProjection {
    val id: Long
    val single: EntityWithCompositeIdProjection?
    val list: List<EntityWithCompositeIdProjection>?
}