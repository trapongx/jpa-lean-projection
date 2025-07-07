package com.runninglane.jpa.projection.test.cases.association.onetomany.map.bidirectional

import com.runninglane.jpa.projection.test.BaseTest
import com.runninglane.jpa.projection.test.EntityManagerWithCounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [MapOneToManyBidirectionalTestConfig::class])
class MapOneToManyBidirectionalTest : BaseTest() {

    @Test
    fun `should correctly project bidirectional one-to-many map with string key and non-projected entity value`() {
        val leftEntity1 = MapOneToManyBidirectionalLeftEntityWithStringKey().apply {
            string = "Fake1"
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapOneToManyBidirectionalLeftEntityWithStringKey().apply {
            string = "Fake2"
        }.also { entityManager.persist(it) }

        val rightValueEntity1 = MapOneToManyBidirectionalRightValueEntityWithStringKey().apply {
            double = 1101.1
            stringKey = "Test1"
            left = leftEntity1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapOneToManyBidirectionalRightValueEntityWithStringKey().apply {
            double = 2202.2
            stringKey = "Test2"
            left = leftEntity1
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapOneToManyBidirectionalRightValueEntityWithStringKey().apply {
            double = 3303.3
            stringKey = "Test3"
            left = leftEntity2
        }.also { entityManager.persist(it) }

        entityManager.flush()

        entityManager.refresh(leftEntity1)
        entityManager.refresh(leftEntity2)

        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapOneToManyBidirectionalLeftEntityWithStringKey::class,
            MapOneToManyBidirectionalLeftEntityWithStringKeyProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapOneToManyBidirectionalLeftEntityWithStringKey::id,
                MapOneToManyBidirectionalLeftEntityWithStringKeyProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key to it.value.id })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key to it.value.id })
                entity.rightMap?.keys?.forEach { key ->
                    val rightValueProjection = projection.rightMap?.get(key)!!
                    val rightValueEntity = entity.rightMap?.get(key)!!
                    assertThat(rightValueProjection.id).isEqualTo(rightValueEntity.id)
                    assertThat(rightValueProjection.double).isEqualTo(rightValueEntity.double)
                    assertThat(rightValueProjection.stringKey).isEqualTo(rightValueEntity.stringKey).isEqualTo(key)
                    assertThat(rightValueProjection.left?.id).isEqualTo(rightValueEntity.left?.id)
                }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            // Assert correct property values in projections
            assertThat(leftProjection1.rightMap?.size).isEqualTo(leftEntity1.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection2.rightMap?.size).isEqualTo(leftEntity2.rightMap?.size).isEqualTo(1)
            assertThat(leftProjection1.string).isEqualTo(leftEntity1.string).isEqualTo("Fake1")
            assertThat(leftProjection2.string).isEqualTo(leftEntity2.string).isEqualTo("Fake2")
            assertThat(leftProjection1.rightMap?.keys).isEqualTo(leftEntity1.rightMap?.keys).isEqualTo(setOf("Test1", "Test2"))
            assertThat(leftProjection2.rightMap?.keys).isEqualTo(leftEntity2.rightMap?.keys).isEqualTo(setOf("Test3"))
            assertThat(leftProjection1.rightMap?.get("Test1")?.id).isEqualTo(rightValueEntity1.id)
            assertThat(leftProjection1.rightMap?.get("Test2")?.id).isEqualTo(rightValueEntity2.id)
            assertThat(leftProjection2.rightMap?.get("Test3")?.id).isEqualTo(rightValueEntity3.id)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project bidirectional one-to-many map with string key and projected entity value`() {
        val leftEntity1 = MapOneToManyBidirectionalLeftEntityWithStringKey().apply {
            string = "Fake1"
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapOneToManyBidirectionalLeftEntityWithStringKey().apply {
            string = "Fake2"
        }.also { entityManager.persist(it) }

        val rightValueEntity1 = MapOneToManyBidirectionalRightValueEntityWithStringKey().apply {
            double = 1101.1
            stringKey = "Test1"
            left = leftEntity1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapOneToManyBidirectionalRightValueEntityWithStringKey().apply {
            double = 2202.2
            stringKey = "Test2"
            left = leftEntity1
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapOneToManyBidirectionalRightValueEntityWithStringKey().apply {
            double = 3303.3
            stringKey = "Test3"
            left = leftEntity2
        }.also { entityManager.persist(it) }

        entityManager.flush()

        entityManager.refresh(leftEntity1)
        entityManager.refresh(leftEntity2)

        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapOneToManyBidirectionalLeftEntityWithStringKey::class,
            MapOneToManyBidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapOneToManyBidirectionalLeftEntityWithStringKey::id,
                MapOneToManyBidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key to it.value.id })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key to it.value.id })
                entity.rightMap?.keys?.forEach { key ->
                    val rightValueProjection = projection.rightMap?.get(key)!!
                    val rightValueEntity = entity.rightMap?.get(key)!!
                    assertThat(rightValueProjection.id).isEqualTo(rightValueEntity.id)
                    assertThat(rightValueProjection.double).isEqualTo(rightValueEntity.double)
                    assertThat(rightValueProjection.stringKey).isEqualTo(rightValueEntity.stringKey).isEqualTo(key)
                    assertThat(rightValueProjection.left?.id).isEqualTo(rightValueEntity.left?.id)
                }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightValueProjection1 = leftProjection1.rightMap?.get("Test1")
            val rightValueProjection2 = leftProjection1.rightMap?.get("Test2")
            val rightValueProjection3 = leftProjection2.rightMap?.get("Test3")

            // Assert that projections of the same entity are reused
            assertThat(rightValueProjection1?.left).isSameAs(rightValueProjection2?.left)

            // Assert correct property values in projections
            assertThat(leftProjection1.rightMap?.size).isEqualTo(leftEntity1.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection2.rightMap?.size).isEqualTo(leftEntity2.rightMap?.size).isEqualTo(1)
            assertThat(leftProjection1.string).isEqualTo(leftEntity1.string).isEqualTo("Fake1")
            assertThat(leftProjection2.string).isEqualTo(leftEntity2.string).isEqualTo("Fake2")
            assertThat(leftProjection1.rightMap?.keys).isEqualTo(leftEntity1.rightMap?.keys).isEqualTo(setOf("Test1", "Test2"))
            assertThat(leftProjection2.rightMap?.keys).isEqualTo(leftEntity2.rightMap?.keys).isEqualTo(setOf("Test3"))
            assertThat(leftProjection1.rightMap?.get("Test1")?.id).isEqualTo(rightValueEntity1.id)
            assertThat(leftProjection1.rightMap?.get("Test2")?.id).isEqualTo(rightValueEntity2.id)
            assertThat(leftProjection2.rightMap?.get("Test3")?.id).isEqualTo(rightValueEntity3.id)
            assertThat(rightValueProjection1?.double).isEqualTo(rightValueEntity1.double).isEqualTo(1101.1)
            assertThat(rightValueProjection2?.double).isEqualTo(rightValueEntity2.double).isEqualTo(2202.2)
            assertThat(rightValueProjection3?.double).isEqualTo(rightValueEntity3.double).isEqualTo(3303.3)
            assertThat(rightValueProjection1?.stringKey).isEqualTo(rightValueEntity1.stringKey).isEqualTo("Test1")
            assertThat(rightValueProjection2?.stringKey).isEqualTo(rightValueEntity2.stringKey).isEqualTo("Test2")
            assertThat(rightValueProjection3?.stringKey).isEqualTo(rightValueEntity3.stringKey).isEqualTo("Test3")
            assertThat(rightValueProjection1?.left?.id).isEqualTo(rightValueEntity1.left?.id).isEqualTo(leftProjection1.id)
            assertThat(rightValueProjection2?.left?.id).isEqualTo(rightValueEntity2.left?.id).isEqualTo(leftProjection1.id)
            assertThat(rightValueProjection3?.left?.id).isEqualTo(rightValueEntity3.left?.id).isEqualTo(leftProjection2.id)
        }

        entityManagerWithCounter.assertQueryCount(2)
    }

    @Test
    fun `should correctly project bidirectional one-to-many map with non-projected entity key and non-projected entity value`() {
        val leftEntity1 = MapOneToManyBidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake1"
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapOneToManyBidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake2"
        }.also { entityManager.persist(it) }

        val rightKeyEntity1 = MapOneToManyBidirectionalRightKeyEntity().apply {
            int = 11
        }.also { entityManager.persist(it) }

        val rightKeyEntity2 = MapOneToManyBidirectionalRightKeyEntity().apply {
            int = 22
        }.also { entityManager.persist(it) }

        val rightKeyEntity3 = MapOneToManyBidirectionalRightKeyEntity().apply {
            int = 33
        }.also { entityManager.persist(it) }

        val rightValueEntity1 = MapOneToManyBidirectionalRightValueEntityWithEntityKey().apply {
            double = 1101.1
            entityKey = rightKeyEntity1
            left = leftEntity1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapOneToManyBidirectionalRightValueEntityWithEntityKey().apply {
            double = 2202.2
            entityKey = rightKeyEntity2
            left = leftEntity1
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapOneToManyBidirectionalRightValueEntityWithEntityKey().apply {
            double = 3303.3
            entityKey = rightKeyEntity3
            left = leftEntity2
        }.also { entityManager.persist(it) }

        entityManager.flush()

        entityManager.refresh(leftEntity1)
        entityManager.refresh(leftEntity2)

        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapOneToManyBidirectionalLeftEntityWithEntityKey::class,
            MapOneToManyBidirectionalLeftEntityWithEntityKeyProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapOneToManyBidirectionalLeftEntityWithEntityKey::id,
                MapOneToManyBidirectionalLeftEntityWithEntityKeyProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key to it.value.id })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key to it.value.id })
                entity.rightMap?.keys?.forEach { key ->
                    val rightValueProjection = projection.rightMap?.get(key)!!
                    val rightValueEntity = entity.rightMap?.get(key)!!
                    assertThat(rightValueProjection.id).isEqualTo(rightValueEntity.id)
                    assertThat(rightValueProjection.double).isEqualTo(rightValueEntity.double)
                    assertThat(rightValueProjection.entityKey?.id).isEqualTo(rightValueEntity.entityKey?.id).isEqualTo(key.id)
                    assertThat(rightValueProjection.left?.id).isEqualTo(rightValueEntity.left?.id)
                }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            // Assert correct property values in projections
            assertThat(leftProjection1.rightMap?.size).isEqualTo(leftEntity1.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection2.rightMap?.size).isEqualTo(leftEntity2.rightMap?.size).isEqualTo(1)
            assertThat(leftProjection1.string).isEqualTo(leftEntity1.string).isEqualTo("Fake1")
            assertThat(leftProjection2.string).isEqualTo(leftEntity2.string).isEqualTo("Fake2")
            assertThat(leftProjection1.rightMap?.keys?.map { it.id!! }?.toSet())
                .isEqualTo(leftEntity1.rightMap?.keys?.map { it.id!! }?.toSet())
                .isEqualTo(setOf(rightKeyEntity1.id, rightKeyEntity2.id))
            assertThat(leftProjection2.rightMap?.keys?.map { it.id!! }?.toSet())
                .isEqualTo(leftEntity2.rightMap?.keys?.map { it.id!! }?.toSet())
                .isEqualTo(setOf(rightKeyEntity3.id))
            assertThat(leftProjection1.rightMap?.get(rightKeyEntity1)?.id).isEqualTo(rightValueEntity1.id)
            assertThat(leftProjection1.rightMap?.get(rightKeyEntity2)?.id).isEqualTo(rightValueEntity2.id)
            assertThat(leftProjection2.rightMap?.get(rightKeyEntity3)?.id).isEqualTo(rightValueEntity3.id)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project bidirectional one-to-many map with projected entity key and projected entity value`() {
        val leftEntity1 = MapOneToManyBidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake1"
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapOneToManyBidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake2"
        }.also { entityManager.persist(it) }

        val rightKeyEntity1 = MapOneToManyBidirectionalRightKeyEntity().apply {
            int = 11
        }.also { entityManager.persist(it) }

        val rightKeyEntity2 = MapOneToManyBidirectionalRightKeyEntity().apply {
            int = 22
        }.also { entityManager.persist(it) }

        val rightKeyEntity3 = MapOneToManyBidirectionalRightKeyEntity().apply {
            int = 33
        }.also { entityManager.persist(it) }

        val rightValueEntity1 = MapOneToManyBidirectionalRightValueEntityWithEntityKey().apply {
            double = 1101.1
            entityKey = rightKeyEntity1
            left = leftEntity1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapOneToManyBidirectionalRightValueEntityWithEntityKey().apply {
            double = 2202.2
            entityKey = rightKeyEntity2
            left = leftEntity1
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapOneToManyBidirectionalRightValueEntityWithEntityKey().apply {
            double = 3303.3
            entityKey = rightKeyEntity3
            left = leftEntity2
        }.also { entityManager.persist(it) }

        entityManager.flush()

        entityManager.refresh(leftEntity1)
        entityManager.refresh(leftEntity2)

        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapOneToManyBidirectionalLeftEntityWithEntityKey::class,
            MapOneToManyBidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapOneToManyBidirectionalLeftEntityWithEntityKey::id,
                MapOneToManyBidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key.id to it.value.id })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key.id to it.value.id })
                entity.rightMap?.keys?.forEach { rightEntityKey ->
                    val rightEntityKeyProjection = projection.rightMap?.keys?.first { it.id == rightEntityKey.id }!!
                    val rightValueProjection = projection.rightMap?.get(rightEntityKeyProjection)!!
                    val rightValueEntity = entity.rightMap?.get(rightEntityKey)!!
                    assertThat(rightValueProjection.id).isEqualTo(rightValueEntity.id)
                    assertThat(rightValueProjection.double).isEqualTo(rightValueEntity.double)
                    assertThat(rightValueProjection.entityKey?.id).isEqualTo(rightValueEntity.entityKey?.id).isEqualTo(rightEntityKey.id)
                    assertThat(rightValueProjection.left?.id).isEqualTo(rightValueEntity.left?.id)
                }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightKeyProjection1 = leftProjection1.rightMap?.keys?.first { it.id == rightKeyEntity1.id }!!
            val rightKeyProjection2 = leftProjection1.rightMap?.keys?.first { it.id == rightKeyEntity2.id }!!
            val rightKeyProjection3 = leftProjection2.rightMap?.keys?.first { it.id == rightKeyEntity3.id }!!

            val rightValueProjection1 = leftProjection1.rightMap?.get(rightKeyProjection1)
            val rightValueProjection2 = leftProjection1.rightMap?.get(rightKeyProjection2)
            val rightValueProjection3 = leftProjection2.rightMap?.get(rightKeyProjection3)

            // Assert that projections of the same entity are reused
            assertThat(rightValueProjection1?.left).isSameAs(rightValueProjection2?.left)
            assertThat(rightValueProjection1?.entityKey)
                .isSameAs(leftProjection1.rightMap?.keys?.first { it.id == rightKeyProjection1.id })
            assertThat(rightValueProjection2?.entityKey)
                .isSameAs(leftProjection1.rightMap?.keys?.first { it.id == rightKeyProjection2.id })
            assertThat(rightValueProjection3?.entityKey)
                .isSameAs(leftProjection2.rightMap?.keys?.first { it.id == rightKeyProjection3.id })

            // Assert correct property values in projections
            assertThat(leftProjection1.rightMap?.size).isEqualTo(leftEntity1.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection2.rightMap?.size).isEqualTo(leftEntity2.rightMap?.size).isEqualTo(1)
            assertThat(leftProjection1.string).isEqualTo(leftEntity1.string).isEqualTo("Fake1")
            assertThat(leftProjection2.string).isEqualTo(leftEntity2.string).isEqualTo("Fake2")
            assertThat(leftProjection1.rightMap?.keys?.map { it.id }?.toSet())
                .isEqualTo(leftEntity1.rightMap?.keys?.map { it.id }?.toSet())
                .isEqualTo(setOf(rightKeyProjection1.id, rightKeyProjection2.id))
            assertThat(leftProjection2.rightMap?.keys?.map { it.id }?.toSet())
                .isEqualTo(leftEntity2.rightMap?.keys?.map { it.id }?.toSet())
                .isEqualTo(setOf(rightKeyProjection3.id))
            assertThat(leftProjection1.rightMap?.get(rightKeyProjection1)?.id).isEqualTo(rightValueEntity1.id)
            assertThat(leftProjection1.rightMap?.get(rightKeyProjection2)?.id).isEqualTo(rightValueEntity2.id)
            assertThat(leftProjection2.rightMap?.get(rightKeyProjection3)?.id).isEqualTo(rightValueEntity3.id)
            assertThat(rightValueProjection1?.double).isEqualTo(rightValueEntity1.double).isEqualTo(1101.1)
            assertThat(rightValueProjection2?.double).isEqualTo(rightValueEntity2.double).isEqualTo(2202.2)
            assertThat(rightValueProjection3?.double).isEqualTo(rightValueEntity3.double).isEqualTo(3303.3)
            assertThat(rightValueProjection1?.entityKey?.id).isEqualTo(rightValueEntity1.entityKey?.id).isEqualTo(rightKeyProjection1.id)
            assertThat(rightValueProjection2?.entityKey?.id).isEqualTo(rightValueEntity2.entityKey?.id).isEqualTo(rightKeyProjection2.id)
            assertThat(rightValueProjection3?.entityKey?.id).isEqualTo(rightValueEntity3.entityKey?.id).isEqualTo(rightKeyProjection3.id)
            assertThat(rightValueProjection1?.left?.id).isEqualTo(rightValueEntity1.left?.id).isEqualTo(leftProjection1.id)
            assertThat(rightValueProjection2?.left?.id).isEqualTo(rightValueEntity2.left?.id).isEqualTo(leftProjection1.id)
            assertThat(rightValueProjection3?.left?.id).isEqualTo(rightValueEntity3.left?.id).isEqualTo(leftProjection2.id)
        }

        entityManagerWithCounter.assertQueryCount(2)
    }
}
