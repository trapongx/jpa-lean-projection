package com.runninglane.jpa.projection.annotation

import com.runninglane.jpa.projection.ProjectorFactory
import javax.persistence.Transient
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.jvm.javaField

internal fun KProperty1<*, *>.isAnnotatedForNoProjection(
    noProjectionAnnotations: Set<KClass<out Annotation>> = emptySet()
): Boolean {
    val annotations = noProjectionAnnotations + setOf(NoProjection::class, Transient::class)

    return annotations.any { annotation ->
        // Direct annotations
        findAnnotations(annotation).isNotEmpty()
                // Getter annotations
                || getter.findAnnotations(annotation).isNotEmpty()
                // Java field annotations
                ||javaField?.isAnnotationPresent(annotation.java) == true
    }
}

internal fun KProperty1<*, *>.isAnnotatedForNoProjection(
    projectorFactory: ProjectorFactory
): Boolean = isAnnotatedForNoProjection(projectorFactory.projectionFactory.noProjectionAnnotations)