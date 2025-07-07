package com.runninglane.jpa.projection.test.cases.embedded

import com.runninglane.jpa.projection.test.BaseTest
import com.runninglane.jpa.projection.test.EntityManagerWithCounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [EmbeddedTestConfig::class])
class ProjectedEmbeddableTest : BaseTest() {

    @Test
    fun `should correctly project entity with one plain projected embedded property`() {
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
            EntityWithOnePlainEmbeddedValuesProjectionWithProjectedEmbedded::class,
            EntityWithOnePlainEmbeddedValues::id,
            EntityWithOnePlainEmbeddedValuesProjectionWithProjectedEmbedded::id,
            2,
            entityManagerWithCounter
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.embedded.long).isEqualTo(entity.embedded.long)
            assertThat(projection.embedded.string).isEqualTo(entity.embedded.string)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project entity with nested projected embedded property`() {
        EntityWithOuterEmbeddedValueWithOneInner().apply {
            embedded = OuterEmbeddableValueWithOneInner(
                inner = EmbeddableValue(1, 2L, "Test1")
            )
        }.also { entityManager.persist(it) }

        EntityWithOuterEmbeddedValueWithOneInner().apply {
            embedded = OuterEmbeddableValueWithOneInner(
                inner = EmbeddableValue(3, 4L, "Test1")
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerifyEach(
            EntityWithOuterEmbeddedValueWithOneInner::class,
            EntityWithOuterEmbeddedValueWithOneInnerProjectionWithProjectedEmbedded::class,
            EntityWithOuterEmbeddedValueWithOneInner::id,
            EntityWithOuterEmbeddedValueWithOneInnerProjectionWithProjectedEmbedded::id,
            2,
            entityManagerWithCounter
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.embedded.inner.long).isEqualTo(entity.embedded.inner.long)
            assertThat(projection.embedded.inner.string).isEqualTo(entity.embedded.inner.string)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project entity with nested projected inside projected embedded property`() {
        EntityWithOuterEmbeddedValueWithOneInner().apply {
            embedded = OuterEmbeddableValueWithOneInner(
                inner = EmbeddableValue(1, 2L, "Test1")
            )
        }.also { entityManager.persist(it) }

        EntityWithOuterEmbeddedValueWithOneInner().apply {
            embedded = OuterEmbeddableValueWithOneInner(
                inner = EmbeddableValue(3, 4L, "Test1")
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerifyEach(
            EntityWithOuterEmbeddedValueWithOneInner::class,
            EntityWithOuterEmbeddedValueWithOneInnerProjectionWithProjectedInsideProjectedEmbedded::class,
            EntityWithOuterEmbeddedValueWithOneInner::id,
            EntityWithOuterEmbeddedValueWithOneInnerProjectionWithProjectedInsideProjectedEmbedded::id,
            2,
            entityManagerWithCounter
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.embedded.inner.long).isEqualTo(entity.embedded.inner.long)
            assertThat(projection.embedded.inner.string).isEqualTo(entity.embedded.inner.string)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }
}
