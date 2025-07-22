package com.runninglane.jpa.projection.annotations

import com.runninglane.jpa.projection.ProjectorFactory
import javax.persistence.Transient
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotations

internal fun KProperty1<*, *>.isAnnotatedForNoProjection(
    noProjectionAnnotations: Set<KClass<out Annotation>> = emptySet()
): Boolean {
    val annotations = noProjectionAnnotations + setOf(NoProjection::class, Transient::class)
    return annotations.any { annotation ->
        findAnnotations(annotation).isNotEmpty()
                || getter.findAnnotations(annotation).isNotEmpty()
    }
}

internal fun KProperty1<*, *>.isAnnotatedForNoProjection(
    projectorFactory: ProjectorFactory
): Boolean = isAnnotatedForNoProjection(projectorFactory.projectionFactory.noProjectionAnnotations)