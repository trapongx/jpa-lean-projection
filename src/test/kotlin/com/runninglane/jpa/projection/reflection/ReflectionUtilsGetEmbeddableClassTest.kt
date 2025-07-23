package com.runninglane.jpa.projection.reflection

import org.junit.jupiter.api.Test
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReflectionUtilsGetEmbeddableClassTest {

    // Class with @Embeddable annotation
    @Embeddable
    open class TestEmbeddable {
        val name: String = "Test"
        val score: Int = 100
    }

    // Regular class with no JPA annotations
    class NonEmbeddableClass {
        val id: Long = 1
        val name: String = "Test"
    }

    // Class that extends an @Embeddable class
    class TestEmbeddableChild : TestEmbeddable() {
        val extraField: Boolean = true
    }

    // Class with @Entity annotation (not @Embeddable)
    @Entity
    class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 1
        val name: String = "Test"
    }

    @Test
    fun `getEmbeddableClass should return itself when class has Embeddable annotation`() {
        // Given a class with @Embeddable annotation
        val embeddableClass: KClass<TestEmbeddable> = TestEmbeddable::class

        // When getting the embeddable class
        val result = embeddableClass.getEmbeddableClass()

        // Then it should return the class itself
        assertEquals(embeddableClass, result)
    }

    @Test
    fun `getEmbeddableClass should return null for regular class`() {
        // Given a regular class with no JPA annotations
        val regularClass: KClass<NonEmbeddableClass> = NonEmbeddableClass::class

        // When getting the embeddable class
        val result = regularClass.getEmbeddableClass()

        // Then it should return null
        assertNull(result)
    }

    @Test
    fun `getEmbeddableClass should return parent embeddable class for subclass of embeddable`() {
        // Given a class that extends an @Embeddable class
        val embeddableChildClass: KClass<TestEmbeddableChild> = TestEmbeddableChild::class

        // When getting the embeddable class
        val result = embeddableChildClass.getEmbeddableClass()

        // Then it should return the parent embeddable class
        assertEquals(TestEmbeddable::class, result)
    }

    @Test
    fun `getEmbeddableClass should return null for entity class`() {
        // Given a class with @Entity annotation (not @Embeddable)
        val entityClass: KClass<TestEntity> = TestEntity::class

        // When getting the embeddable class
        val result = entityClass.getEmbeddableClass()

        // Then it should return null
        assertNull(result)
    }

    @Test
    fun `getEmbeddableClass should return null for null superclasses`() {
        // Create a mock class with no superclasses that isn't @Embeddable
        class IsolatedClass {
            val value: String = "no inheritance"
        }

        // When getting the embeddable class
        val result = IsolatedClass::class.getEmbeddableClass()

        // Then it should return null
        assertNull(result)
    }
}