package com.runninglane.jpa.projection.mapper.cases.simplevalue

import com.runninglane.jpa.projection.mapper.BaseTest
import com.runninglane.jpa.projection.mapper.assert.expectEntityClassMapper
import org.junit.jupiter.api.Test
import javax.persistence.Id

internal class SimpleValueTest : BaseTest() {

    interface ProjectionWithExactTypes {
        val id: Long
        val name: String
        val age: Int
    }

    // Mock implementation of projection interface for testing
    open class MockProjectionWithExactTypes : ProjectionWithExactTypes {
        override var id: Long = 0
        override var name: String = ""
        override var age: Int = 0
    }

    // Simple entity class for testing
    class SimpleEntity {
        @Id
        var id: Long = 0
        var name: String = ""
        var age: Int = 0
    }

    @Test
    fun `it should create SameTypePropertyMapper for each property`() {
        val entityClassMapper = createEntityClassMapperWithMocks(
            SimpleEntity::class,
            ProjectionWithExactTypes::class,
            MockProjectionWithExactTypes::class,
            MockProjectionWithExactTypes()
        )

        val mapperAssertion = expectEntityClassMapper(3) { mapper ->
            expectSameTypePropertyMapper("id")
            expectSameTypePropertyMapper("age")
            expectSameTypePropertyMapper("name")
        }

        mapperAssertion.assert(entityClassMapper)
    }

}