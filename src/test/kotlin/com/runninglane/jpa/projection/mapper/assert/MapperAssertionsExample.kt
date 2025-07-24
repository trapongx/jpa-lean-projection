package com.runninglane.jpa.projection.mapper.assert

import com.runninglane.jpa.projection.mapper.BaseTest
import com.runninglane.jpa.projection.mapper.association.AnyToOnePropertyMapperSimplifiedWithJoinFetch
import org.junit.jupiter.api.Test
import javax.persistence.Id
import javax.persistence.ManyToOne
import kotlin.test.assertEquals

internal class MapperAssertionsExample : BaseTest() {

    /**
     * This example demonstrates how to use the MapperAssertions utility
     * to validate complex mapper structures
     */

    // Example entity classes
    class Person {
        @Id
        var id: Long = 0
        var name: String = ""
        var age: Int = 0
        @ManyToOne
        var country: Country? = null
    }

    class Country {
        @Id
        var id: Long = 0
        var name: String = ""
        var code: String = ""
    }

    // Example projection interfaces
    interface PersonProjection {
        val id: Long
        val name: String
        val age: Int
        val country: CountryProjection?
    }

    interface CountryProjection {
        val id: Long
        val name: String
    }

    // Mock implementations for testing
    class MockPersonProjection : PersonProjection {
        override var id: Long = 0
        override var name: String = ""
        override var age: Int = 0
        override var country: CountryProjection? = null
    }

    class MockCountryProjection : CountryProjection {
        override var id: Long = 0
        override var name: String = ""
    }

    @Test
    fun `demonstrate complex mapper assertions`() {
        // Given expectations for the mapper structure
        val mapperExpectation = expectEntityClassMapper(4) { mapper ->
            // Direct assertions on this mapper (EntityClassMapper)
            assertEquals(Person::class, mapper.entityClass)
            assertEquals(PersonProjection::class, mapper.projectionClass)

            // Expect simple properties
            expectSameTypePropertyMapper("id")

            // Expect an association to Country
            expectMapper(
                "country",
                AnyToOnePropertyMapperSimplifiedWithJoinFetch::class
            ) { mapper ->
                // You can add nested expectations for the country mapper
                expectEntityClassMapper(entityClass = Country::class, projectionClass = CountryProjection::class) { mapper ->
                    // Direct assertions on this Country mapper
                    assertEquals(Country::class, mapper.entityClass)

                    expectSameTypePropertyMapper("id")
                    expectSameTypePropertyMapper("name")
                }
            }

            expectSameTypePropertyMapper("age")
            expectSameTypePropertyMapper("name")
        }

        // When
        val personMapper = createEntityClassMapperWithMocks(
            Person::class,
            PersonProjection::class,
            MockPersonProjection::class,
            MockPersonProjection(),
            mappings = mapOf(
                (Country::class to CountryProjection::class) to (MockCountryProjection::class to { MockCountryProjection() })
            )
        )

        // Then
        mapperExpectation.assert(personMapper)
    }
}
