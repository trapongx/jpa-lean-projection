package com.runninglane.jpa.projection.reflection

import org.junit.jupiter.api.Test
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Table
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReflectionUtilsKClassAnnotationTest {

    // Test classes with various annotations
    @Entity
    @Table(name = "test_entity")
    class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 1
    }

    @MappedSuperclass
    class TestMappedSuperclass {
        val id: Long = 1
    }

    class RegularClass {
        val id: Long = 1
    }

    @Test
    fun `annotatedWith should return true when class has annotation`() {
        // Given a class with annotation
        val entityClass: KClass<TestEntity> = TestEntity::class

        // When checking if it's annotated with Entity
        val result = entityClass.annotatedWith<Entity>()

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `annotatedWith should return true when class has one of multiple annotations`() {
        // Given a class with multiple annotations
        val entityClass: KClass<TestEntity> = TestEntity::class

        // When checking if it's annotated with different annotations
        val resultEntity = entityClass.annotatedWith<Entity>()
        val resultTable = entityClass.annotatedWith<Table>()

        // Then it should return true for both
        assertTrue(resultEntity)
        assertTrue(resultTable)
    }

    @Test
    fun `annotatedWith should return false when class has no annotations`() {
        // Given a class with no annotations
        val regularClass: KClass<RegularClass> = RegularClass::class

        // When checking if it's annotated with any annotation
        val resultEntity = regularClass.annotatedWith<Entity>()
        val resultMappedSuperclass = regularClass.annotatedWith<MappedSuperclass>()

        // Then it should return false for all checks
        assertFalse(resultEntity)
        assertFalse(resultMappedSuperclass)
    }

    @Test
    fun `annotatedWith should return false when checking for wrong annotation`() {
        // Given a class with an Entity annotation
        val entityClass: KClass<TestEntity> = TestEntity::class

        // When checking if it's annotated with a different annotation
        val resultMappedSuperclass = entityClass.annotatedWith<MappedSuperclass>()

        // Then it should return false
        assertFalse(resultMappedSuperclass)
    }

    @Test
    fun `getAnnotation reified should return annotation when class has annotation`() {
        // Given a class with annotation
        val entityClass: KClass<TestEntity> = TestEntity::class

        // When getting the Entity annotation using the reified method
        val annotation = entityClass.getAnnotation<Entity>()

        // Then it should return the annotation
        assertNotNull(annotation)
    }

    @Test
    fun `getAnnotation reified should return specific annotation when class has multiple annotations`() {
        // Given a class with multiple annotations
        val entityClass: KClass<TestEntity> = TestEntity::class

        // When getting specific annotations using the reified method
        val entityAnnotation = entityClass.getAnnotation<Entity>()
        val tableAnnotation = entityClass.getAnnotation<Table>()

        // Then it should return the specific annotations
        assertNotNull(entityAnnotation)
        assertNotNull(tableAnnotation)
        assertEquals("test_entity", tableAnnotation?.name)
    }

    @Test
    fun `getAnnotation reified should return null when class has no matching annotation`() {
        // Given a class with no annotations
        val regularClass: KClass<RegularClass> = RegularClass::class

        // When getting annotations that don't exist using the reified method
        val entityAnnotation = regularClass.getAnnotation<Entity>()
        val mappedSuperclassAnnotation = regularClass.getAnnotation<MappedSuperclass>()

        // Then it should return null
        assertNull(entityAnnotation)
        assertNull(mappedSuperclassAnnotation)
    }

    @Test
    fun `getAnnotation non-reified should return annotation when class has annotation`() {
        // Given a class with annotation
        val entityClass: KClass<TestEntity> = TestEntity::class

        // When getting the Entity annotation using the non-reified method
        val annotation = entityClass.getAnnotation(Entity::class)

        // Then it should return the annotation
        assertNotNull(annotation)
    }

    @Test
    fun `getAnnotation non-reified should return specific annotation when class has multiple annotations`() {
        // Given a class with multiple annotations
        val entityClass: KClass<TestEntity> = TestEntity::class

        // When getting specific annotations using the non-reified method
        val entityAnnotation = entityClass.getAnnotation(Entity::class)
        val tableAnnotation = entityClass.getAnnotation(Table::class)

        // Then it should return the specific annotations
        assertNotNull(entityAnnotation)
        assertNotNull(tableAnnotation)
        assertEquals("test_entity", tableAnnotation?.name)
    }

    @Test
    fun `getAnnotation non-reified should return null when class has no matching annotation`() {
        // Given a class with no annotations
        val regularClass: KClass<RegularClass> = RegularClass::class

        // When getting annotations that don't exist using the non-reified method
        val entityAnnotation = regularClass.getAnnotation(Entity::class)
        val mappedSuperclassAnnotation = regularClass.getAnnotation(MappedSuperclass::class)

        // Then it should return null
        assertNull(entityAnnotation)
        assertNull(mappedSuperclassAnnotation)
    }
}