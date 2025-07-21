package com.runninglane.jpa.projection.annotations

import javax.persistence.Transient
import kotlin.reflect.KProperty1

private val noProjectionAnnotations = setOf(NoProjection::class, Transient::class)

internal fun KProperty1<*, *>.isAnnotatedForNoProjection(): Boolean =
    (annotations + getter.annotations).any { it.annotationClass in noProjectionAnnotations }