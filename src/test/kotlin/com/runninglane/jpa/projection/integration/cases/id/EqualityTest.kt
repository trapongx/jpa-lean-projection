package com.runninglane.jpa.projection.integration.cases.id

import com.runninglane.jpa.projection.queryWithProjection
import com.runninglane.jpa.projection.integration.BaseTest
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
        assertThat(projection1).isNotEqualTo(projection2)
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
    fun `2 projections of different entities with embedded ID should be not equals`() {
        val entity1 = EntityWithEmbeddedId().apply {
            id = EmbeddableId().apply {
                id1 = 1
                id2 = 2
            }
            name = "Test 1"
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithEmbeddedId().apply {
            id = EmbeddableId().apply {
                id1 = 3
                id2 = 4
            }
            name = "Test 2"
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val projections = entityManager.queryWithProjection<EntityWithEmbeddedId, EntityWithEmbeddedIdProjection>()

        assertThat(projections).hasSize(2)

        val projection1 = projections.first { it.id == entity1.id }
        val projection2 = projections.single { it.id == entity2.id}

        assertThat(projection1).isNotSameAs(projection2)

        val projection1SecondQuery = entityManager.queryWithProjection<EntityWithEmbeddedId, EntityWithEmbeddedIdProjection>(
            predicateBuilder = { cb, query, root ->
                cb.equal(root.get<Long>("id"), entity1.id)
            }
        ).single()

        assertThat(projection1).isNotSameAs(projection1SecondQuery)
        assertThat(projection1).isEqualTo(projection1SecondQuery)
        assertThat(projection1).isNotEqualTo(projection2)
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

    @Test
    fun `2 projections of different entities with composite ID should be not equals`() {
        val entity1 = EntityWithCompositeId().apply {
            id1 = 1
            id2 = 2
            name = "Test 1"
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithCompositeId().apply {
            id1 = 3
            id2 = 4
            name = "Test 2"
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val projections = entityManager.queryWithProjection<EntityWithCompositeId, EntityWithCompositeIdProjection>()

        assertThat(projections).hasSize(2)

        val projection1 = projections.first { it.id1 == entity1.id1 && it.id2 == entity1.id2 }
        val projection2 = projections.single { it.id1 == entity2.id1 && it.id2 == entity2.id2 }

        assertThat(projection1).isNotSameAs(projection2)

        val projection1SecondQuery = entityManager.queryWithProjection<EntityWithCompositeId, EntityWithCompositeIdProjection>(
            predicateBuilder = { cb, query, root ->
                cb.and(
                cb.equal(root.get<Long>("id1"), entity1.id1),
                    cb.equal(root.get<Long>("id2"), entity1.id2)
                )
            }
        ).single()

        assertThat(projection1).isNotSameAs(projection1SecondQuery)
        assertThat(projection1).isEqualTo(projection1SecondQuery)
        assertThat(projection1).isNotEqualTo(projection2)
    }

    @Test
    fun `should project associations to entities having composite ID correctly`() {
        val entity1 = EntityWithCompositeId().apply {
            id1 = 1
            id2 = 2
            name = "Test 1"
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithCompositeId().apply {
            id1 = 3
            id2 = 4
            name = "Test 2"
        }.also { entityManager.persist(it) }

        val holder = EntityHoldingEntityWithCompositeId().apply {
            single = entity1
            list = listOf(entity1, entity2)
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val holderProjection = entityManager.queryWithProjection<EntityHoldingEntityWithCompositeId, EntityHoldingEntityWithCompositeIdProjection>(
            predicateBuilder = { cb, query, root ->
                cb.equal(root.get<Long>("id"), holder.id)
            }
        ).single()

        assertThat(holderProjection.list).hasSize(2)

        val projection1 = holderProjection.list?.first { it.id1 == entity1.id1 && it.id2 == entity1.id2 }
        val projection2 = holderProjection.list?.first { it.id1 == entity2.id1 && it.id2 == entity2.id2 }

        assertThat(holderProjection.single).isSameAs(projection1)
        assertThat(projection1?.name).isEqualTo(entity1.name)
        assertThat(projection2?.name).isEqualTo(entity2.name)
    }
}
