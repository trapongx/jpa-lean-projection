package com.runninglane.jpa.projection.integration.cases.elementcollection.collection

import java.time.LocalDateTime
interface EntityWithSetOfLocalDateTimeProjection {
    val id: Long
    val elements: Set<LocalDateTime>?
}