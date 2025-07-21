package com.runninglane.jpa.projection.reflection

import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.MappedSuperclass
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

/**
 * @return itself or superclass that declared with @Entity or @MappedSuperclass
 */
internal fun KClass<*>.getEntityClass(): KClass<*>? {
    return if (annotatedWith<Entity>() || annotatedWith<MappedSuperclass>()) {
        this
    } else {
        superclasses.firstOrNull { it.annotatedWith<Entity>() || it.annotatedWith<MappedSuperclass>() }
    }
}

/**
 * @return itself of superclass that declared with @Embeddable
 */
internal fun KClass<*>.getEmbeddableClass(): KClass<*>? {
    return if (annotatedWith<Embeddable>()) {
        this
    } else {
        superclasses.firstOrNull { it.annotatedWith<Embeddable>() }
    }
}

// KProperty extensions

internal inline fun <reified T> KProperty<*>.annotatedWith(): Boolean where T : Annotation =
    hasAnnotation<T>() || javaField?.getAnnotation(T::class.java) != null || getter.getAnnotation<T>() != null

internal inline fun <reified T> KProperty<*>.getAnnotation(): T? where T : Annotation =
    findAnnotation() ?: javaField?.getAnnotation(T::class.java) ?: getter.getAnnotation()

// KFunction extensions

internal inline fun <reified T> KFunction<*>.annotatedWith(): Boolean where T : Annotation =
    hasAnnotation<T>() || javaMethod?.getAnnotation(T::class.java) != null

internal inline fun <reified T> KFunction<*>.getAnnotation(): T? where T : Annotation =
    findAnnotation() ?: javaMethod?.getAnnotation(T::class.java)

// KClass extensions

internal inline fun <reified T> KClass<*>.annotatedWith(): Boolean where T : Annotation =
    hasAnnotation<T>() || java.getAnnotation(T::class.java) != null

internal inline fun <reified T> KClass<*>.getAnnotation(): T? where T : Annotation =
    findAnnotation() ?: java.getAnnotation(T::class.java)

internal fun <T> KClass<*>.getAnnotation(annotationType: KClass<T>): T? where T : Annotation =
    annotations.filter { it.annotationClass == annotationType }
        .takeIf { it.isNotEmpty() }?.single()?.let {
            @Suppress("UNCHECKED_CAST")
            it as T
        }
        ?: java.getAnnotation(annotationType.java)

fun KClass<*>.getPropertyAtPath(path: String): KProperty1<*, *> {
    return path.split('.')
        .fold(null as KProperty1<*, *>? to this) { (_, currentClass), name ->
            val nextProp = currentClass.memberProperties.firstOrNull { it.name == name }
                ?: error("Property $name not found in class ${currentClass.qualifiedName}")
            val nextClass = nextProp.returnType.jvmErasure
            nextProp to nextClass
        }.first!!
}