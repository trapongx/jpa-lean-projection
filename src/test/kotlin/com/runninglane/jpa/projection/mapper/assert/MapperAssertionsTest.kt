package com.runninglane.jpa.projection.mapper.assert

import com.runninglane.jpa.projection.mapper.BaseTest
import com.runninglane.jpa.projection.mapper.PropertyMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opentest4j.AssertionFailedError
import javax.persistence.Id
import kotlin.test.assertEquals

internal class MapperAssertionsTest : BaseTest() {

    // Simple entity class for testing
    class SimpleEntity {
        @Id
        var id: Long = 0
        var name: String = ""
        var age: Int = 0
    }

    interface SimpleProjection {
        val id: Long
        val name: String
        val age: Int
    }

    // Mock implementation of projection interface for testing
    open class MockSimpleProjection : SimpleProjection {
        override var id: Long = 0
        override var name: String = ""
        override var age: Int = 0
    }

    @Test
    fun `should verify mapper structure`() {
        // Given
        val entityClassMapper = createEntityClassMapperWithMocks(
            SimpleEntity::class,
            SimpleProjection::class,
            MockSimpleProjection::class,
            MockSimpleProjection()
        )

        // Define expectations
        val mapperExpectation = expectEntityClassMapper(3) { mapper ->
            // Direct assertions on the mapper (this refers to EntityClassMapper)
            assertEquals(SimpleEntity::class, mapper.entityClass)
            assertEquals(SimpleProjection::class, mapper.projectionClass)

            expectSameTypePropertyMapper("id")
            expectSameTypePropertyMapper("age")
            expectSameTypePropertyMapper("name")
        }

        // When/Then
        mapperExpectation.assert(entityClassMapper)
    }

    @Test
    fun `should fail when mapper has wrong number of children`() {
        // Given
        val entityClassMapper = createEntityClassMapperWithMocks(
            SimpleEntity::class,
            SimpleProjection::class,
            MockSimpleProjection::class,
            MockSimpleProjection()
        )

        // Define expectations
        val mapperExpectation = expectEntityClassMapper(4) { // Expecting 4 children but mapper has 3
            expectSameTypePropertyMapper("id")
            expectSameTypePropertyMapper("name")
            expectSameTypePropertyMapper("age")
        }

        // When/Then
        assertThrows<AssertionError> {
            mapperExpectation.assert(entityClassMapper)
        }
    }

    @Test
    fun `should fail when property name does not match`() {
        // Given
        val entityClassMapper = createEntityClassMapperWithMocks(
            SimpleEntity::class,
            SimpleProjection::class,
            MockSimpleProjection::class,
            MockSimpleProjection()
        )

        // Define expectations
        val mapperExpectation = expectEntityClassMapper(3) {
            expectSameTypePropertyMapper("id")
            expectSameTypePropertyMapper("wrongName") // Wrong property name
            expectSameTypePropertyMapper("age")
        }

        // When/Then
        assertThrows<AssertionFailedError> {
            mapperExpectation.assert(entityClassMapper)
        }
    }

    @Test
    fun `should support custom assertions with expectMapper`() {
        // Given
        val entityClassMapper = createEntityClassMapperWithMocks(
            SimpleEntity::class,
            SimpleProjection::class,
            MockSimpleProjection::class,
            MockSimpleProjection()
        )

        // Define expectations
        val mapperExpectation = expectEntityClassMapper(3) { mapper ->
            // Direct assertions on this EntityClassMapper
            assertEquals(SimpleEntity::class, mapper.entityClass)

            expectMapper { mapper ->
                assertEquals("id", (mapper as PropertyMapper).propertyName)
            }
            expectMapper { mapper ->
                assertEquals("age", (mapper as PropertyMapper).propertyName)
            }
            expectMapper { mapper ->
                assertEquals("name", (mapper as PropertyMapper).propertyName)
            }
        }

        // When/Then
        mapperExpectation.assert(entityClassMapper)
    }
}
