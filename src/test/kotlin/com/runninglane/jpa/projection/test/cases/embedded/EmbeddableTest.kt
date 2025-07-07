package com.runninglane.jpa.projection.test.cases.embedded

import com.runninglane.jpa.projection.test.BaseTest
import com.runninglane.jpa.projection.test.EntityManagerWithCounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import java.lang.reflect.InvocationTargetException

@DataJpaTest
@ContextConfiguration(classes = [EmbeddedTestConfig::class])
class EmbeddableTest : BaseTest() {

    @Test
    fun `should correctly project one plain embedded property`() {
        EntityWithOnePlainEmbeddedValues().apply {
            embedded = EmbeddableValue(1, 2L, "Test1")
        }.also { entityManager.persist(it) }

        EntityWithOnePlainEmbeddedValues().apply {
            embedded = EmbeddableValue(5, 6L, "Test3")
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerifyEach(
            EntityWithOnePlainEmbeddedValues::class,
            EntityWithOnePlainEmbeddedValuesProjection::class,
            EntityWithOnePlainEmbeddedValues::id,
            EntityWithOnePlainEmbeddedValuesProjection::id,
            2,
            entityManagerWithCounter
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.embedded).isEqualTo(entity.embedded)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project two plain embedded properties`() {
        EntityWithTwoPlainEmbeddedValues().apply {
            embedded1 = EmbeddableValue(1, 2L, "Test1")
            embedded2 = EmbeddableValue(3, 4L, "Test2")
        }.also { entityManager.persist(it) }

        EntityWithTwoPlainEmbeddedValues().apply {
            embedded1 = EmbeddableValue(5, 6L, "Test3")
            embedded2 = EmbeddableValue(7, 8L, "Test4")
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerifyEach(
            EntityWithTwoPlainEmbeddedValues::class,
            EntityWithTwoPlainEmbeddedValuesProjection::class,
            EntityWithTwoPlainEmbeddedValues::id,
            EntityWithTwoPlainEmbeddedValuesProjection::id,
            2,
            entityManagerWithCounter
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.embedded1).isEqualTo(entity.embedded1)
            assertThat(projection.embedded2).isEqualTo(entity.embedded2)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project two plain embedded properties inverted nullability`() {
        EntityWithTwoPlainEmbeddedValues().apply {
            embedded1 = EmbeddableValue(1, 2L, "Test1")
            embedded2 = EmbeddableValue(3, 4L, "Test2")
        }.also { entityManager.persist(it) }

        EntityWithTwoPlainEmbeddedValues().apply {
            embedded1 = EmbeddableValue(5, 6L, "Test3")
            embedded2 = EmbeddableValue(7, 8L, "Test4")
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerifyEach(
            EntityWithTwoPlainEmbeddedValues::class,
            EntityWithTwoPlainEmbeddedValuesProjectionWithInvertedNullability::class,
            EntityWithTwoPlainEmbeddedValues::id,
            EntityWithTwoPlainEmbeddedValuesProjectionWithInvertedNullability::id,
            2,
            entityManagerWithCounter
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.embedded1).isEqualTo(entity.embedded1)
            assertThat(projection.embedded2).isEqualTo(entity.embedded2)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should map null plain embedded property successfully`() {
        EntityWithTwoPlainEmbeddedValues().apply {
            embedded1 = EmbeddableValue(1, 2L, "Test1")
            embedded2 = EmbeddableValue(3, 4L, "Test2")
        }.also { entityManager.persist(it) }

        EntityWithTwoPlainEmbeddedValues().apply {
            embedded1 = EmbeddableValue(5, 6L, "Test3")
            embedded2 = null
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerifyEach(
            EntityWithTwoPlainEmbeddedValues::class,
            EntityWithTwoPlainEmbeddedValuesProjection::class,
            EntityWithTwoPlainEmbeddedValues::id,
            EntityWithTwoPlainEmbeddedValuesProjection::id,
            2,
            entityManagerWithCounter
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.embedded1).isEqualTo(entity.embedded1)
            assertThat(projection.embedded2).isEqualTo(entity.embedded2)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should fail mapping plain embedded property nullable to non-nullable`() {
        EntityWithTwoPlainEmbeddedValues().apply {
            embedded1 = EmbeddableValue(1, 2L, "Test1")
            embedded2 = EmbeddableValue(3, 4L, "Test2")
        }.also { entityManager.persist(it) }

        EntityWithTwoPlainEmbeddedValues().apply {
            embedded1 = EmbeddableValue(5, 6L, "Test3")
            embedded2 = null
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndExpectException(
            EntityWithTwoPlainEmbeddedValues::class,
            EntityWithTwoPlainEmbeddedValuesProjectionWithInvertedNullability::class,
            InvocationTargetException::class,
            entityManagerWithCounter
        )

        entityManagerWithCounter.assertQueryCount(1)
    }

}
