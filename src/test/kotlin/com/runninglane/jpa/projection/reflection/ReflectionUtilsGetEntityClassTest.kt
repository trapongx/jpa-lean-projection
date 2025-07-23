package com.runninglane.jpa.projection.reflection

import org.junit.jupiter.api.Test
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReflectionUtilsGetEntityClassTest {

    // Class with @Entity annotation
    @Entity
    open class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 1
        val name: String = "Test"
    }

    // Class with @MappedSuperclass annotation
    @MappedSuperclass
    open class TestMappedSuperclass {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 1
    }

    // Class that extends a @MappedSuperclass
    class TestEntitySubclass : TestMappedSuperclass() {
        val name: String = "Test"
    }

    // Regular class with no JPA annotations
    class NonEntityClass {
        val id: Long = 1
        val name: String = "Test"
    }

    // Class that extends an @Entity class
    class TestEntityChild : TestEntity() {
        val age: Int = 30
    }

    @Test
    fun `getEntityClass should return itself when class has Entity annotation`() {
        // Given a class with @Entity annotation
        val entityClass: KClass<TestEntity> = TestEntity::class

        // When getting the entity class
        val result = entityClass.getEntityClass()

        // Then it should return the class itself
        assertEquals(entityClass, result)
    }

    @Test
    fun `getEntityClass should return itself when class has MappedSuperclass annotation`() {
        // Given a class with @MappedSuperclass annotation
        val mappedSuperclass: KClass<TestMappedSuperclass> = TestMappedSuperclass::class

        // When getting the entity class
        val result = mappedSuperclass.getEntityClass()

        // Then it should return the class itself
        assertEquals(mappedSuperclass, result)
    }

    @Test
    fun `getEntityClass should return null for regular class`() {
        // Given a regular class with no JPA annotations
        val regularClass: KClass<NonEntityClass> = NonEntityClass::class

        // When getting the entity class
        val result = regularClass.getEntityClass()

        // Then it should return null
        assertNull(result)
    }

    @Test
    fun `getEntityClass should return parent entity class for subclass of entity`() {
        // Given a class that extends an @Entity class
        val entityChildClass: KClass<TestEntityChild> = TestEntityChild::class

        // When getting the entity class
        val result = entityChildClass.getEntityClass()

        // Then it should return the parent entity class
        assertEquals(TestEntity::class, result)
    }

    @Test
    fun `getEntityClass should return parent mapped superclass for subclass of mapped superclass`() {
        // Given a class that extends a @MappedSuperclass
        val subclassClass: KClass<TestEntitySubclass> = TestEntitySubclass::class

        // When getting the entity class
        val result = subclassClass.getEntityClass()

        // Then it should return the parent mapped superclass
        assertEquals(TestMappedSuperclass::class, result)
    }
}