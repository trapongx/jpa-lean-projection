package com.runninglane.jpa.projection.test.cases.equality

import com.runninglane.jpa.projection.queryWithProjection
import com.runninglane.jpa.projection.test.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [EqualityTestConfig::class])
class EqualityTest : BaseTest() {

    @Test
    fun `2 projections of the same entity with single ID should be equals`() {
        val entity = EntityWithSingleId().apply {
            name = "Test 1"
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val projection = entityManager.queryWithProjection<EntityWithSingleId, EntityWithSingleIdProjection>(
            predicateBuilder = { cb, query, root ->
                cb.equal(root.get<Long>("id"), entity.id)
            }
        ).single()

        val projectionSecondQuery = entityManager.queryWithProjection<EntityWithSingleId, EntityWithSingleIdProjection>(
            predicateBuilder = { cb, query, root ->
                cb.equal(root.get<Long>("id"), entity.id)
            }
        ).single()

        assertThat(projection).isNotSameAs(projectionSecondQuery)
        assertThat(projection).isEqualTo(projectionSecondQuery)
    }

    @Test
    fun `2 projections of different entities with single ID should be not equals`() {
        val entity1 = EntityWithSingleId().apply {
            name = "Test 1"
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithSingleId().apply {
            name = "Test 2"
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val projections = entityManager.queryWithProjection<EntityWithSingleId, EntityWithSingleIdProjection>()

        assertThat(projections).hasSize(2)

        val projection1 = projections.first { it.id == entity1.id }
        val projection2 = projections.single { it.id == entity2.id}

        assertThat(projection1).isNotSameAs(projection2)

        val projection1SecondQuery = entityManager.queryWithProjection<EntityWithSingleId, EntityWithSingleIdProjection>(
            predicateBuilder = { cb, query, root ->
                cb.equal(root.get<Long>("id"), entity1.id)
            }
        ).single()

        assertThat(projection1).isNotSameAs(projection1SecondQuery)
        assertThat(projection1).isEqualTo(projection1SecondQuery)
    }

    @Test
    fun `2 projections of the same entity with embedded ID should be equals`() {
        val entity = EntityWithEmbeddedId().apply {
            id = EmbeddableId().apply {
                id1 = 1
                id2 = 2
            }
            name = "Test 1"
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val projection = entityManager.queryWithProjection<EntityWithEmbeddedId, EntityWithEmbeddedIdProjection>(
            predicateBuilder = { cb, query, root ->
                cb.equal(root.get<EmbeddableId>("id"), entity.id)
            }
        ).single()

        val projectionSecondQuery = entityManager.queryWithProjection<EntityWithEmbeddedId, EntityWithEmbeddedIdProjection>(
            predicateBuilder = { cb, query, root ->
                cb.equal(root.get<EmbeddableId>("id"), entity.id)
            }
        ).single()

        assertThat(projection).isNotSameAs(projectionSecondQuery)
        assertThat(projection).isEqualTo(projectionSecondQuery)
    }

    @Test
    fun `2 projections of the same entity with composite ID should be equals`() {
        val entity = EntityWithCompositeId().apply {
            id1 = 1
            id2 = 2
            name = "Test 1"
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val projection = entityManager.queryWithProjection<EntityWithCompositeId, EntityWithCompositeIdProjection>(
            predicateBuilder = { cb, query, root ->
                cb.and(
                    cb.equal(root.get<Long>("id1"), entity.id1),
                    cb.equal(root.get<Long>("id2"), entity.id2)
                )
            }
        ).single()

        val projectionSecondQuery = entityManager.queryWithProjection<EntityWithCompositeId, EntityWithCompositeIdProjection>(
            predicateBuilder = { cb, query, root ->
                cb.and(
                    cb.equal(root.get<Long>("id1"), entity.id1),
                    cb.equal(root.get<Long>("id2"), entity.id2)
                )
            }
        ).single()

        assertThat(projection).isNotSameAs(projectionSecondQuery)
        assertThat(projection).isEqualTo(projectionSecondQuery)
    }
}
