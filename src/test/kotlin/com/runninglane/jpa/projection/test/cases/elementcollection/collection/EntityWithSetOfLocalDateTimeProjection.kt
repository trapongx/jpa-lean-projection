package com.runninglane.jpa.projection.test.cases.elementcollection.collection

import java.time.LocalDateTime
interface EntityWithSetOfLocalDateTimeProjection {
    val id: Long
    val elements: Set<LocalDateTime>?
}