package com.runninglane.jpa.projection.annotation

import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.reflection.annotatedWith
import javax.persistence.Transient
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal fun KProperty1<*, *>.isAnnotatedForNoProjection(
    noProjectionAnnotations: Set<KClass<out Annotation>> = emptySet()
): Boolean = (noProjectionAnnotations + setOf(NoProjection::class, Transient::class))
    .any { annotatedWith(it) }

internal fun KProperty1<*, *>.isAnnotatedForNoProjection(
    projectorFactory: ProjectorFactory
): Boolean = isAnnotatedForNoProjection(projectorFactory.projectionFactory.noProjectionAnnotations)