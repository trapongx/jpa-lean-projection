package com.runninglane.jpa.projection.integration.cases.id

interface EntityHoldingEntityWithCompositeIdProjection {
    val id: Long
    val single: EntityWithCompositeIdProjection?
    val list: List<EntityWithCompositeIdProjection>?
}