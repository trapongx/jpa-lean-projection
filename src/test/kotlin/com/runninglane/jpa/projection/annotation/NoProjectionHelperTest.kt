package com.runninglane.jpa.projection.annotation

import com.runninglane.jpa.projection.ProjectionFactory
import com.runninglane.jpa.projection.ProjectorFactory
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import javax.persistence.Transient
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoProjectionHelperTest {

    // Test class with various property annotations
    class TestClass {
        @NoProjection
        val noProjectionProperty: String = "skip me"

        @Transient
        val transientProperty: Int = 42

        // Property with getter annotation
        val getterAnnotatedProperty: Boolean
            @NoProjection
            get() = true

        // Property with getter annotation for Transient
        val getterTransientProperty: Double
            @Transient
            get() = 3.14

        // Property with custom annotation that should be treated as NoProjection
        @CustomNoProjection
        val customAnnotatedProperty: String = "custom annotation"

        // Regular property with no relevant annotations
        val regularProperty: String = "include me"
    }

    // Custom annotation for testing custom NoProjection annotations
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class CustomNoProjection

    @Test
    fun `isAnnotatedForNoProjection should return true for properties with NoProjection annotation`() {
        // Given a property with NoProjection annotation
        val property = TestClass::class.memberProperties.first { it.name == "noProjectionProperty" }

        // When checking if it's annotated for no projection
        val result = property.isAnnotatedForNoProjection()

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `isAnnotatedForNoProjection should return true for properties with Transient annotation`() {
        // Given a property with Transient annotation
        val property = TestClass::class.memberProperties.first { it.name == "transientProperty" }

        // When checking if it's annotated for no projection
        val result = property.isAnnotatedForNoProjection()

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `isAnnotatedForNoProjection should return true for properties with NoProjection annotation on getter`() {
        // Given a property with NoProjection annotation on getter
        val property = TestClass::class.memberProperties.first { it.name == "getterAnnotatedProperty" }

        // When checking if it's annotated for no projection
        val result = property.isAnnotatedForNoProjection()

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `isAnnotatedForNoProjection should return true for properties with Transient annotation on getter`() {
        // Given a property with Transient annotation on getter
        val property = TestClass::class.memberProperties.first { it.name == "getterTransientProperty" }

        // When checking if it's annotated for no projection
        val result = property.isAnnotatedForNoProjection()

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `isAnnotatedForNoProjection should return false for regular properties`() {
        // Given a regular property with no relevant annotations
        val property = TestClass::class.memberProperties.first { it.name == "regularProperty" }

        // When checking if it's annotated for no projection
        val result = property.isAnnotatedForNoProjection()

        // Then it should return false
        assertFalse(result)
    }

    @Test
    fun `isAnnotatedForNoProjection should recognize custom NoProjection annotations when provided`() {
        // Given a property with custom annotation
        val property = TestClass::class.memberProperties.first { it.name == "customAnnotatedProperty" }

        // When checking with custom NoProjection annotations
        val resultWithout = property.isAnnotatedForNoProjection() // without custom annotation in the set
        val resultWith = property.isAnnotatedForNoProjection(setOf(CustomNoProjection::class))

        // Then it should return false without custom annotation and true with it
        assertFalse(resultWithout)
        assertTrue(resultWith)
    }

    @Test
    fun `isAnnotatedForNoProjection should use annotations from ProjectorFactory`() {
        // Given a property with custom annotation
        val property = TestClass::class.memberProperties.first { it.name == "customAnnotatedProperty" }

        // And a mock ProjectorFactory with custom NoProjection annotations
        val projectionFactory = mock(ProjectionFactory::class.java)
        val projectorFactory = mock(ProjectorFactory::class.java)
        `when`(projectorFactory.projectionFactory).thenReturn(projectionFactory)
        `when`(projectionFactory.noProjectionAnnotations).thenReturn(setOf(CustomNoProjection::class as KClass<out Annotation>))

        // When checking with the ProjectorFactory
        val result = property.isAnnotatedForNoProjection(projectorFactory)

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `isAnnotatedForNoProjection should check for Transient annotation in different places`() {
        // Given a property with Transient annotation
        val property = TestClass::class.memberProperties.first { it.name == "transientProperty" }

        // When checking if it's annotated for no projection
        val result = property.isAnnotatedForNoProjection()

        // Then it should return true
        assertTrue(result)

        // Additional debug info to understand where annotations are found
        println("Direct annotations on property: ${property.annotations.map { it.annotationClass.simpleName }}")
        println("Annotations on javaField: ${property.javaField?.annotations?.map { it.annotationClass.simpleName }}")
        println("Annotations on getter: ${property.getter.annotations.map { it.annotationClass.simpleName }}")
    }
}