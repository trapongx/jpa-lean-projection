package com.runninglane.jpa.projection.integration.cases.annotatedgettersetter

import com.runninglane.jpa.projection.queryWithProjection
import com.runninglane.jpa.projection.integration.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [AnnotatedGetterSetterTestConfiguration::class])
class AnnotatedGetterSetterTest : BaseTest() {

    @Test
    fun `should correctly recognize projection superinterfaces with custom property of type entity`() {
        val entity1 = TestEntity().apply {
            previousEntryId = null
        }.also { entityManager.persist(it) }

        entityManager.flush()

        val entity2 = TestEntity().apply {
            previousEntryId = entity1.id
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val projection1 = entityManager.queryWithProjection<TestEntity, TestEntityProjectionWithAnnotatedGetterSetter>(
            { cb, _, root ->
                cb.equal(root.get<Long>("id"), entity1.id)
            }
        ).single()

        assertThat(projection1.isFirstEntry).isTrue()

        val projection2 = entityManager.queryWithProjection<TestEntity, TestEntityProjectionWithAnnotatedGetterSetter>(
            { cb, _, root ->
                cb.equal(root.get<Long>("id"), entity2.id)
            }
        ).single()

        assertThat(projection2.isFirstEntry).isFalse()
    }

}
