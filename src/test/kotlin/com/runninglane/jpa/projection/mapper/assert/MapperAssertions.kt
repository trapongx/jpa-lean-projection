package com.runninglane.jpa.projection.mapper.assert

import com.runninglane.jpa.projection.mapper.EntityClassMapper
import com.runninglane.jpa.projection.mapper.Mapper
import com.runninglane.jpa.projection.mapper.assert.MapperFormatter.formatTree
import com.runninglane.jpa.projection.mapper.sametype.SameTypePropertyMapper
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * MapperAssertion represents an expectation about a mapper's structure
 */
internal class MapperAssertion<T : Mapper>(
    private val expectedType: KClass<T>, 
    private val childrenCount: Int? = null
) : (T) -> Unit {
    private val selfAssertions = mutableListOf<(T) -> Unit>()
    private val childAssertions = mutableListOf<ChildAssertion>()

    // Allow direct assertions on the mapper instance
    var mapper: T? = null
        private set

    /**
     * Define assertions on the current mapper
     */
    fun self(assertion: (T) -> Unit) {
        selfAssertions.add(assertion)
    }

    /**
     * Execute assertions on the current mapper
     */
    override fun invoke(value: T) {
        selfAssertions.add { assertEquals(value, it) }
    }

    /**
     * Expect a specific type of child property mapper with its own assertions
     */
    fun <C : Mapper> expectMapper(propertyName: String?, expectedType: KClass<C>, config: (MapperAssertion<C>.(C) -> Unit)? = null): MapperAssertion<T> {
        val childAssertion = MapperAssertion<C>(expectedType)
        if (config != null) {
            childAssertion.self { mapper -> config(childAssertion, mapper) }
        }
        childAssertions.add(ChildAssertion(propertyName, childAssertion))
        return this
    }

    /**
     * Expect a specific type of child mapper with its own assertions
     */
    fun <C : Mapper> expectMapper(expectedType: KClass<C>, config: (MapperAssertion<C>.(C) -> Unit)? = null): MapperAssertion<T> {
        return expectMapper(null, expectedType, config)
    }

    /**
     * Expect a generic mapper with custom assertions
     */
    fun expectMapper(config: (Mapper) -> Unit): MapperAssertion<T> {
        childAssertions.add(ChildAssertion(null, config))
        return this
    }

    /**
     * Expect a SameTypePropertyMapper with the given property name
     */
    fun expectSameTypePropertyMapper(propertyName: String): MapperAssertion<T> {
        childAssertions.add(ChildAssertion(propertyName, { mapper: Mapper ->
            assertIs<SameTypePropertyMapper>(mapper, "Expected SameTypePropertyMapper for property $propertyName")
            assertEquals(propertyName, mapper.propertyName, "Property name should match")
        }))
        return this
    }

    /**
     * Expect an EntityClassMapper with optional configuration
     */
    fun expectEntityClassMapper(
        entityClass: KClass<*>? = null,
        projectionClass: KClass<*>? = null,
        childrenCount: Int? = null,
        config: (MapperAssertion<EntityClassMapper>.(EntityClassMapper) -> Unit)? = null
    ): MapperAssertion<T> {
        val childAssertion = MapperAssertion<EntityClassMapper>(EntityClassMapper::class, childrenCount)
        if (entityClass != null || projectionClass != null) {
            childAssertion.self { mapper ->
                entityClass?.let { assertEquals(it, mapper.entityClass, "Entity class should match") }
                projectionClass?.let { assertEquals(it, mapper.projectionClass, "Projection class should match") }
            }
        }
        if (config != null) {
            childAssertion.self { mapper -> config(childAssertion, mapper) }
        }
        childAssertions.add(ChildAssertion(null, childAssertion))
        return this
    }

    /**
     * Asserts that the given mapper matches this expectation
     */
    fun assert(actualMapper: Mapper) {
        assertInternal(actualMapper) {
            // Check the mapper type
            assertEquals(
                expectedType.java, actualMapper::class.java,
                "Expected mapper type does not match. Expected: $expectedType, Actual: ${actualMapper::class}"
            )

            // Check children count if specified
            val children = actualMapper.getChildren()
            childrenCount?.let { expected ->
                assertEquals(expected, children.size, "Expected $expected children but found ${children.size}")
            }

            // Store mapper for direct assertions and run self assertions
            @Suppress("UNCHECKED_CAST")
            this.mapper = actualMapper as T
            selfAssertions.forEach { it(this.mapper!!) }

            childAssertions.forEachIndexed { i, childAssertion ->
                val childMapper = if (children.size > i) children[i] else error(childAssertion.noMoreChildren(i + 1))
                childAssertion.assert(childMapper)
            }

            (children.size - childAssertions.size).takeIf { it > 0 }?.let { diff ->
                val firstUnassertedChild = children[childAssertions.size]
                val message = buildString {
                    appendLine("Unexpected child ${firstUnassertedChild::class.simpleName} at index ${childAssertions.size}")
                    val rootMapper = generateSequence(firstUnassertedChild) { it.getParent() }.last()
                    append(formatTree(rootMapper, 0, firstUnassertedChild))
                }
                throw MapperAssertionError(message, null, firstUnassertedChild)
            }

            if (childAssertions.size < children.size) {
                error("Expected ${childAssertions.size} children but found ${children.size}")
            }
        }
    }

    /**
     * Represents an assertion about a child mapper
     * The assertion can be either a MapperAssertion or a function
     */
    private class ChildAssertion(
        val propertyName: String?,
        private val assertion: Any // Either MapperAssertion<*> or (Mapper) -> Unit
    ) {
        @Suppress("UNCHECKED_CAST")
        fun assert(mapper: Mapper) {
            assertInternal(mapper) {
                when (assertion) {
                    is MapperAssertion<*> -> assertion.assert(mapper)
                    is Function1<*, *> -> (assertion as (Mapper) -> Unit)(mapper)
                    else -> error("Unsupported assertion type: ${assertion::class}")
                }
            }
        }

        fun noMoreChildren(index: Int) {
            when (assertion) {
                is MapperAssertion<*> ->
                    error("Expected ${assertion.expectedType.simpleName}${propertyName?.let { "($it)"} } as the ${index}th child but no more children found")
                is Function1<*, *> ->
                    error("Expected the ${index}th child but no more children found")
                else -> error("Unsupported assertion type: ${assertion::class}")
            }
        }
    }
}

/**
 * Creates an expectation for an EntityClassMapper
 * The config block provides the MapperAssertion as receiver (this) and the mapper as parameter
 */
internal fun expectEntityClassMapper(
    childrenCount: Int? = null,
    config: MapperAssertion<EntityClassMapper>.(EntityClassMapper) -> Unit = { _ -> }
): MapperAssertion<EntityClassMapper> {
    return MapperAssertion(EntityClassMapper::class, childrenCount).apply {
        self { mapper -> config(this, mapper) }
    }
}


internal fun assertInternal(currentMapper: Mapper, block: () -> Unit) {
    try {
        block()
    } catch (e: MapperAssertionError) {
        throw e
    } catch (t: Throwable) {
        val rootMapper = generateSequence(currentMapper) { it.getParent() }.last()
        val message = buildString {
            appendLine(t.message ?: "Mapper assertion failed")
            append(formatTree(rootMapper, 0, currentMapper))
        }
        throw MapperAssertionError(message, t, currentMapper)
    }

}
