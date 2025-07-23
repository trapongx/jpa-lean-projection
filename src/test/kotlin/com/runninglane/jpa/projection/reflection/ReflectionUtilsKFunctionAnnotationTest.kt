package com.runninglane.jpa.projection.reflection

import org.junit.jupiter.api.Test
import javax.persistence.PostLoad
import javax.persistence.PostPersist
import javax.persistence.PrePersist
import kotlin.reflect.full.declaredFunctions
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class ReflectionUtilsKFunctionAnnotationTest {

    // Test class with various method annotations
    class TestClass {
        @PrePersist
        fun beforeSave() {
            // Method with direct annotation
        }

        @PostPersist
        @PostLoad
        fun afterSave() {
            // Method with multiple annotations
        }

        fun regularMethod() {
            // Method with no annotations
        }
    }

    @Test
    fun `annotatedWith should return true when method has annotation`() {
        // Given a method with annotation
        val beforeSaveMethod = TestClass::class.declaredFunctions.first { it.name == "beforeSave" }

        // When checking if it's annotated with PrePersist
        val result = beforeSaveMethod.annotatedWith<PrePersist>()

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `annotatedWith should return true when method has one of multiple annotations`() {
        // Given a method with multiple annotations
        val afterSaveMethod = TestClass::class.declaredFunctions.first { it.name == "afterSave" }

        // When checking if it's annotated with different annotations
        val resultPostPersist = afterSaveMethod.annotatedWith<PostPersist>()
        val resultPostLoad = afterSaveMethod.annotatedWith<PostLoad>()

        // Then it should return true for both
        assertTrue(resultPostPersist)
        assertTrue(resultPostLoad)
    }

    @Test
    fun `annotatedWith should return false when method has no annotations`() {
        // Given a method with no annotations
        val regularMethod = TestClass::class.declaredFunctions.first { it.name == "regularMethod" }

        // When checking if it's annotated with any annotation
        val resultPrePersist = regularMethod.annotatedWith<PrePersist>()
        val resultPostLoad = regularMethod.annotatedWith<PostLoad>()

        // Then it should return false for all checks
        assertFalse(resultPrePersist)
        assertFalse(resultPostLoad)
    }

    @Test
    fun `annotatedWith should return false when checking for wrong annotation`() {
        // Given a method with a PrePersist annotation
        val beforeSaveMethod = TestClass::class.declaredFunctions.first { it.name == "beforeSave" }

        // When checking if it's annotated with different annotations
        val resultPostLoad = beforeSaveMethod.annotatedWith<PostLoad>()

        // Then it should return false
        assertFalse(resultPostLoad)
    }

    @Test
    fun `getAnnotation should return annotation when method has annotation`() {
        // Given a method with annotation
        val beforeSaveMethod = TestClass::class.declaredFunctions.first { it.name == "beforeSave" }

        // When getting the PrePersist annotation
        val annotation = beforeSaveMethod.getAnnotation<PrePersist>()

        // Then it should return the annotation
        assertNotNull(annotation)
    }

    @Test
    fun `getAnnotation should return specific annotation when method has multiple annotations`() {
        // Given a method with multiple annotations
        val afterSaveMethod = TestClass::class.declaredFunctions.first { it.name == "afterSave" }

        // When getting specific annotations
        val postPersistAnnotation = afterSaveMethod.getAnnotation<PostPersist>()
        val postLoadAnnotation = afterSaveMethod.getAnnotation<PostLoad>()

        // Then it should return the specific annotations
        assertNotNull(postPersistAnnotation)
        assertNotNull(postLoadAnnotation)
    }

    @Test
    fun `getAnnotation should return null when method has no matching annotation`() {
        // Given a method with no annotations
        val regularMethod = TestClass::class.declaredFunctions.first { it.name == "regularMethod" }

        // When getting annotations that don't exist
        val prePersistAnnotation = regularMethod.getAnnotation<PrePersist>()
        val postLoadAnnotation = regularMethod.getAnnotation<PostLoad>()

        // Then it should return null
        assertNull(prePersistAnnotation)
        assertNull(postLoadAnnotation)
    }

    @Test
    fun `getAnnotation should return null when checking for wrong annotation`() {
        // Given a method with a PrePersist annotation
        val beforeSaveMethod = TestClass::class.declaredFunctions.first { it.name == "beforeSave" }

        // When getting an annotation that doesn't exist
        val postLoadAnnotation = beforeSaveMethod.getAnnotation<PostLoad>()

        // Then it should return null
        assertNull(postLoadAnnotation)
    }
}