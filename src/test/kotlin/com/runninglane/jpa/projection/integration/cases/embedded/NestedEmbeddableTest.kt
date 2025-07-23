package com.runninglane.jpa.projection.integration.cases.embedded

import com.runninglane.jpa.projection.integration.BaseTest
import com.runninglane.jpa.projection.integration.EntityManagerWithCounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [EmbeddedTestConfig::class])
class NestedEmbeddableTest : BaseTest() {

    @Test
    fun `should map nested embedded property with one inner successfully`() {
        EntityWithOuterEmbeddedValueWithOneInner().apply {
            embedded = OuterEmbeddableValueWithOneInner(
                inner = EmbeddableValue(1, 2L, "Test1")
            )
        }.also { entityManager.persist(it) }

        EntityWithOuterEmbeddedValueWithOneInner().apply {
            embedded = OuterEmbeddableValueWithOneInner(
                inner = EmbeddableValue(3, 5L, "Test2")
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerifyEach(
            EntityWithOuterEmbeddedValueWithOneInner::class,
            EntityWithOuterEmbeddedValueWithOneInnerProjection::class,
            EntityWithOuterEmbeddedValueWithOneInner::id,
            EntityWithOuterEmbeddedValueWithOneInnerProjection::id,
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
    fun `should map nested embedded property with two inner successfully`() {
        EntityWithOuterEmbeddedValueWithTwoInner().apply {
            embedded = OuterEmbeddableValueWithTwoInner(
                inner1 = EmbeddableValue(1, 2L, "Test1"),
                inner2 = EmbeddableValue(3, 4L, "Test2")
            )
        }.also { entityManager.persist(it) }

        EntityWithOuterEmbeddedValueWithTwoInner().apply {
            embedded = OuterEmbeddableValueWithTwoInner(
                inner1 = EmbeddableValue(5, 6L, "Test3"),
                inner2 = EmbeddableValue(7, 8L, "Test4")
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerifyEach(
            EntityWithOuterEmbeddedValueWithTwoInner::class,
            EntityWithOuterEmbeddedValueWithTwoInnerProjection::class,
            EntityWithOuterEmbeddedValueWithTwoInner::id,
            EntityWithOuterEmbeddedValueWithTwoInnerProjection::id,
            2,
            entityManagerWithCounter
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.embedded).isEqualTo(entity.embedded)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

}
