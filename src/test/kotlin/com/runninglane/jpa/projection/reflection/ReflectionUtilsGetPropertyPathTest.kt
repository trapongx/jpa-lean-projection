package com.runninglane.jpa.projection.reflection

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass
import kotlin.test.assertEquals

class ReflectionUtilsGetPropertyPathTest {
    // Test classes to verify property path navigation
    class Address(val street: String, val city: String, val zipCode: String)
    class Person(val name: String, val age: Int, val address: Address)
    class Company(val name: String, val ceo: Person, val headquarters: Address)

    @Test
    fun `getPropertyAtPath should navigate simple property path`() {
        // Given a class with properties
        val personClass: KClass<Person> = Person::class

        // When retrieving a direct property
        val nameProperty = personClass.getPropertyAtPath("name")

        // Then the property should be correctly retrieved
        assertEquals("name", nameProperty.name)
        val person = Person("John Doe", 30, Address("123 Main St", "Anytown", "12345"))
        assertEquals("John Doe", nameProperty.getter.call(person))
    }

    @Test
    fun `getPropertyAtPath should navigate nested property path`() {
        // Given a class with nested properties
        val personClass: KClass<Person> = Person::class

        // When retrieving a nested property
        val streetProperty = personClass.getPropertyAtPath("address.street")

        // Then the nested property should be correctly retrieved
        assertEquals("street", streetProperty.name)
        val person = Person("John Doe", 30, Address("123 Main St", "Anytown", "12345"))
        assertEquals("123 Main St", streetProperty.getter.call(person.address))
    }

    @Test
    fun `getPropertyAtPath should navigate deeply nested property path`() {
        // Given a class with deeply nested properties
        val companyClass: KClass<Company> = Company::class

        // When retrieving a deeply nested property
        val ceoAddressCityProperty = companyClass.getPropertyAtPath("ceo.address.city")

        // Then the deeply nested property should be correctly retrieved
        assertEquals("city", ceoAddressCityProperty.name)
        val company = Company(
            "Acme Inc", 
            Person("Jane Smith", 45, Address("456 Broadway", "Metropolis", "67890")),
            Address("789 Corporate Way", "Business City", "54321")
        )
        assertEquals("Metropolis", ceoAddressCityProperty.getter.call(company.ceo.address))
    }

    @Test
    fun `getPropertyAtPath should throw error for non-existent property`() {
        // Given a class
        val personClass: KClass<Person> = Person::class

        // When trying to retrieve a non-existent property
        // Then an error should be thrown
        val exception = assertThrows<IllegalStateException> { 
            personClass.getPropertyAtPath("nonExistentProperty") 
        }
        assertEquals("Property nonExistentProperty not found in class com.runninglane.jpa.projection.reflection.ReflectionUtilsGetPropertyPathTest.Person", exception.message)
    }

    @Test
    fun `getPropertyAtPath should throw error for non-existent nested property`() {
        // Given a class with nested properties
        val personClass: KClass<Person> = Person::class

        // When trying to retrieve a non-existent nested property
        // Then an error should be thrown
        val exception = assertThrows<IllegalStateException> { 
            personClass.getPropertyAtPath("address.nonExistentProperty") 
        }
        assertEquals("Property nonExistentProperty not found in class com.runninglane.jpa.projection.reflection.ReflectionUtilsGetPropertyPathTest.Address", exception.message)
    }
}