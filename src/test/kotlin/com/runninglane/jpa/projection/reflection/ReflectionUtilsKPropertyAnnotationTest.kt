package com.runninglane.jpa.projection.reflection

import org.junit.jupiter.api.Test
import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.Transient
import kotlin.reflect.full.memberProperties
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReflectionUtilsKPropertyAnnotationTest {

    // Test class with various property annotations
    class TestClass {
        @Id
        val id: Long = 1

        @Column(name = "full_name", length = 100)
        val name: String = "Test"

        // Annotation on field
        @field:Transient
        val tempValue: Int = 0

        // Annotation on getter
        val derivedValue: String
            @Transient
            get() = "Derived $name"

        // Property with no annotations
        val noAnnotation: Boolean = false
    }

    @Test
    fun `annotatedWith should return true when property has direct annotation`() {
        // Given a property with direct annotation
        val idProperty = TestClass::class.memberProperties.first { it.name == "id" }

        // When checking if it's annotated with Id
        val result = idProperty.annotatedWith<Id>()

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `annotatedWith should return true when property field has annotation`() {
        // Given a property with field annotation
        val tempValueProperty = TestClass::class.memberProperties.first { it.name == "tempValue" }

        // When checking if it's annotated with Transient
        val result = tempValueProperty.annotatedWith<Transient>()

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `annotatedWith should return true when property getter has annotation`() {
        // Given a property with getter annotation
        val derivedValueProperty = TestClass::class.memberProperties.first { it.name == "derivedValue" }

        // When checking if it's annotated with Transient
        val result = derivedValueProperty.annotatedWith<Transient>()

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `annotatedWith should return false when property has no matching annotation`() {
        // Given a property with no annotations
        val noAnnotationProperty = TestClass::class.memberProperties.first { it.name == "noAnnotation" }

        // When checking if it's annotated with any annotation
        val resultId = noAnnotationProperty.annotatedWith<Id>()
        val resultTransient = noAnnotationProperty.annotatedWith<Transient>()
        val resultColumn = noAnnotationProperty.annotatedWith<Column>()

        // Then it should return false for all checks
        assertFalse(resultId)
        assertFalse(resultTransient)
        assertFalse(resultColumn)
    }

    @Test
    fun `annotatedWith should return false when checking for wrong annotation`() {
        // Given a property with an Id annotation
        val idProperty = TestClass::class.memberProperties.first { it.name == "id" }

        // When checking if it's annotated with different annotations
        val resultTransient = idProperty.annotatedWith<Transient>()

        // Then it should return false
        assertFalse(resultTransient)
    }

    @Test
    fun `getAnnotation should return annotation when property has direct annotation`() {
        // Given a property with direct annotation
        val idProperty = TestClass::class.memberProperties.first { it.name == "id" }

        // When getting the Id annotation
        val annotation = idProperty.getAnnotation<Id>()

        // Then it should return the annotation
        assertNotNull(annotation)
    }

    @Test
    fun `getAnnotation should return annotation when property field has annotation`() {
        // Given a property with field annotation
        val tempValueProperty = TestClass::class.memberProperties.first { it.name == "tempValue" }

        // When getting the Transient annotation
        val annotation = tempValueProperty.getAnnotation<Transient>()

        // Then it should return the annotation
        assertNotNull(annotation)
    }

    @Test
    fun `getAnnotation should return annotation when property getter has annotation`() {
        // Given a property with getter annotation
        val derivedValueProperty = TestClass::class.memberProperties.first { it.name == "derivedValue" }

        // When getting the Transient annotation
        val annotation = derivedValueProperty.getAnnotation<Transient>()

        // Then it should return the annotation
        assertNotNull(annotation)
    }

    @Test
    fun `getAnnotation should return null when property has no matching annotation`() {
        // Given a property with no annotations
        val noAnnotationProperty = TestClass::class.memberProperties.first { it.name == "noAnnotation" }

        // When getting annotations that don't exist
        val idAnnotation = noAnnotationProperty.getAnnotation<Id>()
        val transientAnnotation = noAnnotationProperty.getAnnotation<Transient>()

        // Then it should return null
        assertNull(idAnnotation)
        assertNull(transientAnnotation)
    }

    @Test
    fun `getAnnotation should access annotation attributes`() {
        // Given a property with a Column annotation that has attributes
        val nameProperty = TestClass::class.memberProperties.first { it.name == "name" }

        // When getting the Column annotation
        val annotation = nameProperty.getAnnotation<Column>()

        // Then it should return the annotation with correct attribute values
        assertNotNull(annotation)
        assertEquals("full_name", annotation.name)
        assertEquals(100, annotation.length)
    }

    // Tests for non-reified annotatedWith method

    @Test
    fun `non-reified annotatedWith should return true when property has direct annotation`() {
        // Given a property with direct annotation
        val idProperty = TestClass::class.memberProperties.first { it.name == "id" }

        // When checking if it's annotated with Id using non-reified method
        val result = idProperty.annotatedWith(Id::class)

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `non-reified annotatedWith should return true when property field has annotation`() {
        // Given a property with field annotation
        val tempValueProperty = TestClass::class.memberProperties.first { it.name == "tempValue" }

        // When checking if it's annotated with Transient using non-reified method
        val result = tempValueProperty.annotatedWith(Transient::class)

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `non-reified annotatedWith should return true when property getter has annotation`() {
        // Given a property with getter annotation
        val derivedValueProperty = TestClass::class.memberProperties.first { it.name == "derivedValue" }

        // When checking if it's annotated with Transient using non-reified method
        val result = derivedValueProperty.annotatedWith(Transient::class)

        // Then it should return true
        assertTrue(result)
    }

    @Test
    fun `non-reified annotatedWith should return false when property has no matching annotation`() {
        // Given a property with no annotations
        val noAnnotationProperty = TestClass::class.memberProperties.first { it.name == "noAnnotation" }

        // When checking if it's annotated with any annotation using non-reified method
        val resultId = noAnnotationProperty.annotatedWith(Id::class)
        val resultTransient = noAnnotationProperty.annotatedWith(Transient::class)
        val resultColumn = noAnnotationProperty.annotatedWith(Column::class)

        // Then it should return false for all checks
        assertFalse(resultId)
        assertFalse(resultTransient)
        assertFalse(resultColumn)
    }

    @Test
    fun `non-reified annotatedWith should return false when checking for wrong annotation`() {
        // Given a property with an Id annotation
        val idProperty = TestClass::class.memberProperties.first { it.name == "id" }

        // When checking if it's annotated with different annotations using non-reified method
        val resultTransient = idProperty.annotatedWith(Transient::class)

        // Then it should return false
        assertFalse(resultTransient)
    }

    // Tests for non-reified getAnnotation method

    @Test
    fun `non-reified getAnnotation should return annotation when property has direct annotation`() {
        // Given a property with direct annotation
        val idProperty = TestClass::class.memberProperties.first { it.name == "id" }

        // When getting the Id annotation using non-reified method
        val annotation = idProperty.getAnnotation(Id::class)

        // Then it should return the annotation
        assertNotNull(annotation)
    }

    @Test
    fun `non-reified getAnnotation should return annotation when property field has annotation`() {
        // Given a property with field annotation
        val tempValueProperty = TestClass::class.memberProperties.first { it.name == "tempValue" }

        // When getting the Transient annotation using non-reified method
        val annotation = tempValueProperty.getAnnotation(Transient::class)

        // Then it should return the annotation
        assertNotNull(annotation)
    }

    @Test
    fun `non-reified getAnnotation should return annotation when property getter has annotation`() {
        // Given a property with getter annotation
        val derivedValueProperty = TestClass::class.memberProperties.first { it.name == "derivedValue" }

        // When getting the Transient annotation using non-reified method
        val annotation = derivedValueProperty.getAnnotation(Transient::class)

        // Then it should return the annotation
        assertNotNull(annotation)
    }

    @Test
    fun `non-reified getAnnotation should return null when property has no matching annotation`() {
        // Given a property with no annotations
        val noAnnotationProperty = TestClass::class.memberProperties.first { it.name == "noAnnotation" }

        // When getting annotations that don't exist using non-reified method
        val idAnnotation = noAnnotationProperty.getAnnotation(Id::class)
        val transientAnnotation = noAnnotationProperty.getAnnotation(Transient::class)

        // Then it should return null
        assertNull(idAnnotation)
        assertNull(transientAnnotation)
    }

    @Test
    fun `non-reified getAnnotation should access annotation attributes`() {
        // Given a property with a Column annotation that has attributes
        val nameProperty = TestClass::class.memberProperties.first { it.name == "name" }

        // When getting the Column annotation using non-reified method
        val annotation = nameProperty.getAnnotation(Column::class)

        // Then it should return the annotation with correct attribute values
        assertNotNull(annotation)
        assertEquals("full_name", annotation.name)
        assertEquals(100, annotation.length)
    }

    @Test
    fun `reified and non-reified methods should return the same results`() {
        // Given properties with different annotation placements
        val idProperty = TestClass::class.memberProperties.first { it.name == "id" }
        val tempValueProperty = TestClass::class.memberProperties.first { it.name == "tempValue" }
        val derivedValueProperty = TestClass::class.memberProperties.first { it.name == "derivedValue" }

        // When comparing results from reified and non-reified methods
        val reifiedIdAnnotated = idProperty.annotatedWith<Id>()
        val nonReifiedIdAnnotated = idProperty.annotatedWith(Id::class)

        val reifiedTransientFieldAnnotated = tempValueProperty.annotatedWith<Transient>()
        val nonReifiedTransientFieldAnnotated = tempValueProperty.annotatedWith(Transient::class)

        val reifiedTransientGetterAnnotated = derivedValueProperty.annotatedWith<Transient>()
        val nonReifiedTransientGetterAnnotated = derivedValueProperty.annotatedWith(Transient::class)

        // Then the results should be the same
        assertEquals(reifiedIdAnnotated, nonReifiedIdAnnotated)
        assertEquals(reifiedTransientFieldAnnotated, nonReifiedTransientFieldAnnotated)
        assertEquals(reifiedTransientGetterAnnotated, nonReifiedTransientGetterAnnotated)

        // Also compare annotation instances
        val reifiedIdAnnotation = idProperty.getAnnotation<Id>()
        val nonReifiedIdAnnotation = idProperty.getAnnotation(Id::class)

        val reifiedTransientFieldAnnotation = tempValueProperty.getAnnotation<Transient>()
        val nonReifiedTransientFieldAnnotation = tempValueProperty.getAnnotation(Transient::class)

        val reifiedTransientGetterAnnotation = derivedValueProperty.getAnnotation<Transient>()
        val nonReifiedTransientGetterAnnotation = derivedValueProperty.getAnnotation(Transient::class)

        // Check that both methods find annotations or both return null
        assertEquals(reifiedIdAnnotation != null, nonReifiedIdAnnotation != null)
        assertEquals(reifiedTransientFieldAnnotation != null, nonReifiedTransientFieldAnnotation != null)
        assertEquals(reifiedTransientGetterAnnotation != null, nonReifiedTransientGetterAnnotation != null)
    }
}
