package com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional

import com.runninglane.jpa.projection.integration.BaseTest
import com.runninglane.jpa.projection.integration.EntityManagerWithCounter
import com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.entitykey.*
import com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.stringkey.MapManyToManyBidirectionalLeftEntityWithStringKey
import com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.stringkey.MapManyToManyBidirectionalLeftEntityWithStringKeyProjection
import com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.stringkey.MapManyToManyBidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue
import com.runninglane.jpa.projection.integration.cases.association.manytomany.map.bidirectional.stringkey.MapManyToManyBidirectionalRightValueEntityForCaseStringKey
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [MapManyToManyBidirectionalTestConfig::class])
class MapManyToManyBidirectionalTest : BaseTest() {

    @Test
    fun `should correctly project bidirectional many-to-many map with string key and non-projected entity value`() {
        val rightValueEntity1 = MapManyToManyBidirectionalRightValueEntityForCaseStringKey().apply {
            double = 1101.1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapManyToManyBidirectionalRightValueEntityForCaseStringKey().apply {
            double = 2202.2
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapManyToManyBidirectionalRightValueEntityForCaseStringKey().apply {
            double = 3303.3
        }.also { entityManager.persist(it) }

        val leftEntity1 = MapManyToManyBidirectionalLeftEntityWithStringKey().apply {
            string = "Fake1"
            rightMap = mapOf(
                "Test1" to rightValueEntity1,
                "Test2" to rightValueEntity2
            )
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapManyToManyBidirectionalLeftEntityWithStringKey().apply {
            string = "Fake2"
            rightMap = mapOf(
                "Test3" to rightValueEntity2,
                "Test4" to rightValueEntity3
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapManyToManyBidirectionalLeftEntityWithStringKey::class,
            MapManyToManyBidirectionalLeftEntityWithStringKeyProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapManyToManyBidirectionalLeftEntityWithStringKey::id,
                MapManyToManyBidirectionalLeftEntityWithStringKeyProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key to it.value.id }?.sortedBy { it.first })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key to it.value.id }?.sortedBy { it.first })
                entity.rightMap?.keys?.forEach { key ->
                    val rightValueProjection = projection.rightMap?.get(key)
                    val rightValueEntity = entity.rightMap?.get(key)
                    assertThat(rightValueProjection?.id).isEqualTo(rightValueEntity?.id)
                    assertThat(rightValueProjection?.double).isEqualTo(rightValueEntity?.double)
                    assertThat(rightValueProjection?.leftSet?.size).isEqualTo(rightValueEntity?.leftSet?.size)
                    assertThat(rightValueProjection?.leftSet?.map { it.id }).isEqualTo(rightValueEntity?.leftSet?.map { it.id })
                }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightValueProjection1 = leftProjection1.rightMap?.get("Test1")
            val rightValueProjection2FromLeftProjection1 = leftProjection1.rightMap?.get("Test2")
            val rightValueProjection2FromLeftProjection2 = leftProjection2.rightMap?.get("Test3")
            val rightValueProjection3 = leftProjection2.rightMap?.get("Test4")

            // Assert that projections of the same entity are reused
            assertThat(rightValueProjection2FromLeftProjection1).isSameAs(rightValueProjection2FromLeftProjection2)

            // Assert correct property values in projections
            assertThat(leftProjection1.rightMap?.size).isEqualTo(leftEntity1.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection2.rightMap?.size).isEqualTo(leftEntity2.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection1.string).isEqualTo(leftEntity1.string).isEqualTo("Fake1")
            assertThat(leftProjection2.string).isEqualTo(leftEntity2.string).isEqualTo("Fake2")
            assertThat(rightValueProjection1?.double).isEqualTo(rightValueEntity1.double).isEqualTo(1101.1)
            assertThat(rightValueProjection2FromLeftProjection1?.double).isEqualTo(rightValueEntity2.double).isEqualTo(2202.2)
            assertThat(rightValueProjection3?.double).isEqualTo(rightValueEntity3.double).isEqualTo(3303.3)
            assertThat(rightValueProjection1?.leftSet?.size).isEqualTo(1)
            assertThat(rightValueProjection2FromLeftProjection1?.leftSet?.size).isEqualTo(2)
            assertThat(rightValueProjection3?.leftSet?.size).isEqualTo(1)

        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project bidirectional many-to-many map with string key and projected entity value`() {
        val rightValueEntity1 = MapManyToManyBidirectionalRightValueEntityForCaseStringKey().apply {
            double = 1101.1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapManyToManyBidirectionalRightValueEntityForCaseStringKey().apply {
            double = 2202.2
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapManyToManyBidirectionalRightValueEntityForCaseStringKey().apply {
            double = 3303.3
        }.also { entityManager.persist(it) }

        val leftEntity1 = MapManyToManyBidirectionalLeftEntityWithStringKey().apply {
            string = "Fake1"
            rightMap = mapOf(
                "Test1" to rightValueEntity1,
                "Test2" to rightValueEntity2
            )
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapManyToManyBidirectionalLeftEntityWithStringKey().apply {
            string = "Fake2"
            rightMap = mapOf(
                "Test3" to rightValueEntity2,
                "Test4" to rightValueEntity3
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()

        entityManager.refresh(rightValueEntity1)
        entityManager.refresh(rightValueEntity2)
        entityManager.refresh(rightValueEntity3)

        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapManyToManyBidirectionalLeftEntityWithStringKey::class,
            MapManyToManyBidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapManyToManyBidirectionalLeftEntityWithStringKey::id,
                MapManyToManyBidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key to it.value.id }?.sortedBy { it.first })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key to it.value.id }?.sortedBy { it.first })
                entity.rightMap?.keys?.forEach { key ->
                    val rightValueProjection = projection.rightMap?.get(key)
                    val rightValueEntity = entity.rightMap?.get(key)
                    assertThat(rightValueProjection?.id).isEqualTo(rightValueEntity?.id)
                    assertThat(rightValueProjection?.double).isEqualTo(rightValueEntity?.double)
                    assertThat(rightValueProjection?.leftSet?.size).isEqualTo(rightValueEntity?.leftSet?.size)
                    assertThat(rightValueProjection?.leftSet?.map { it.id }?.sorted()).isEqualTo(rightValueEntity?.leftSet?.map { it.id!! }?.sorted())
                }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightValueProjection1 = leftProjection1.rightMap?.get("Test1")
            val rightValueProjection2FromLeftProjection1 = leftProjection1.rightMap?.get("Test2")
            val rightValueProjection2FromLeftProjection2 = leftProjection2.rightMap?.get("Test3")
            val rightValueProjection3 = leftProjection2.rightMap?.get("Test4")

            // Assert that projections of the same entity are reused
            assertThat(rightValueProjection2FromLeftProjection1).isSameAs(rightValueProjection2FromLeftProjection2)

            assertThat(rightValueProjection1?.leftSet?.first { it.id == leftEntity1.id })
                .isSameAs(rightValueProjection2FromLeftProjection1?.leftSet?.first { it.id == leftEntity1.id })
                .isSameAs(leftProjection1)

            assertThat(rightValueProjection3?.leftSet?.first { it.id == leftEntity2.id })
                .isSameAs(rightValueProjection2FromLeftProjection1?.leftSet?.first { it.id == leftEntity2.id })
                .isSameAs(leftProjection2)

            // Assert correct property values in projections
            assertThat(leftProjection1.rightMap?.size).isEqualTo(leftEntity1.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection2.rightMap?.size).isEqualTo(leftEntity2.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection1.string).isEqualTo(leftEntity1.string).isEqualTo("Fake1")
            assertThat(leftProjection2.string).isEqualTo(leftEntity2.string).isEqualTo("Fake2")
            assertThat(rightValueProjection1?.double).isEqualTo(rightValueEntity1.double).isEqualTo(1101.1)
            assertThat(rightValueProjection2FromLeftProjection1?.double).isEqualTo(rightValueEntity2.double).isEqualTo(2202.2)
            assertThat(rightValueProjection3?.double).isEqualTo(rightValueEntity3.double).isEqualTo(3303.3)
            assertThat(rightValueProjection1?.leftSet?.size).isEqualTo(1)
            assertThat(rightValueProjection2FromLeftProjection1?.leftSet?.size).isEqualTo(2)
            assertThat(rightValueProjection3?.leftSet?.size).isEqualTo(1)
            assertThat(rightValueProjection1?.leftSet?.map { it.id }?.sorted())
                .isEqualTo(rightValueEntity1.leftSet?.map { it.id!! }?.sorted())
            assertThat(rightValueProjection2FromLeftProjection1?.leftSet?.map { it.id }?.sorted())
                .isEqualTo(rightValueEntity2.leftSet?.map { it.id!! }?.sorted())
            assertThat(rightValueProjection3?.leftSet?.map { it.id }?.sorted())
                .isEqualTo(rightValueEntity3.leftSet?.map { it.id!! }?.sorted())
        }

        entityManagerWithCounter.assertQueryCount(3)
    }

    @Test
    fun `should correctly project bidirectional many-to-many map with non-projected entity key and non-projected entity value`() {
        val rightKeyEntity1 = MapManyToManyBidirectionalRightKeyEntity().apply {
            int = 11
        }.also { entityManager.persist(it) }

        val rightKeyEntity2 = MapManyToManyBidirectionalRightKeyEntity().apply {
            int = 22
        }.also { entityManager.persist(it) }

        val rightKeyEntity3 = MapManyToManyBidirectionalRightKeyEntity().apply {
            int = 33
        }.also { entityManager.persist(it) }

        val rightValueEntity1 = MapManyToManyBidirectionalRightValueEntityForCaseEntityKey().apply {
            double = 1101.1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapManyToManyBidirectionalRightValueEntityForCaseEntityKey().apply {
            double = 2202.2
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapManyToManyBidirectionalRightValueEntityForCaseEntityKey().apply {
            double = 3303.3
        }.also { entityManager.persist(it) }

        val leftEntity1 = MapManyToManyBidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake1"
            rightMap = mapOf(
                rightKeyEntity1 to rightValueEntity1,
                rightKeyEntity2 to rightValueEntity2
            )
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapManyToManyBidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake2"
            rightMap = mapOf(
                rightKeyEntity2 to rightValueEntity2,
                rightKeyEntity3 to rightValueEntity3
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()

        entityManager.refresh(rightValueEntity1)
        entityManager.refresh(rightValueEntity2)
        entityManager.refresh(rightValueEntity3)

        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapManyToManyBidirectionalLeftEntityWithEntityKey::class,
            MapManyToManyBidirectionalLeftEntityWithEntityKeyProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapManyToManyBidirectionalLeftEntityWithEntityKey::id,
                MapManyToManyBidirectionalLeftEntityWithEntityKeyProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key.id to it.value.id }?.sortedBy { it.first })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key.id to it.value.id }?.sortedBy { it.first })
                entity.rightMap?.keys?.forEach { key ->
                    val rightValueProjection = projection.rightMap?.get(key)
                    val rightValueEntity = entity.rightMap?.get(key)
                    assertThat(rightValueProjection?.id).isEqualTo(rightValueEntity?.id)
                    assertThat(rightValueProjection?.double).isEqualTo(rightValueEntity?.double)
                    assertThat(rightValueProjection?.leftSet?.size).isEqualTo(rightValueEntity?.leftSet?.size)
                    assertThat(rightValueProjection?.leftSet?.map { it.id }).isEqualTo(rightValueEntity?.leftSet?.map { it.id })
                }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightKeyProjection1 = leftProjection1.rightMap?.keys?.first { it.id == rightKeyEntity1.id }
            val rightKeyProjection2 = leftProjection1.rightMap?.keys?.first { it.id == rightKeyEntity2.id }
            val rightKeyProjection3 = leftProjection2.rightMap?.keys?.first { it.id == rightKeyEntity3.id }

            val rightValueProjection1 = leftProjection1.rightMap?.get(rightKeyProjection1)
            val rightValueProjection2FromLeftProjection1 = leftProjection1.rightMap?.get(rightKeyProjection2)
            val rightValueProjection2FromLeftProjection2 = leftProjection2.rightMap?.get(rightKeyProjection2)
            val rightValueProjection3 = leftProjection2.rightMap?.get(rightKeyProjection3)

            // Assert that projections of the same entity are reused
            assertThat(rightValueProjection2FromLeftProjection1).isSameAs(rightValueProjection2FromLeftProjection2)

            // Assert correct property values in projections
            assertThat(leftProjection1.rightMap?.size).isEqualTo(leftEntity1.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection2.rightMap?.size).isEqualTo(leftEntity2.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection1.string).isEqualTo(leftEntity1.string).isEqualTo("Fake1")
            assertThat(leftProjection2.string).isEqualTo(leftEntity2.string).isEqualTo("Fake2")
            assertThat(rightKeyProjection1?.int).isEqualTo(rightKeyEntity1.int).isEqualTo(11)
            assertThat(rightKeyProjection2?.int).isEqualTo(rightKeyEntity2.int).isEqualTo(22)
            assertThat(rightKeyProjection3?.int).isEqualTo(rightKeyEntity3.int).isEqualTo(33)
            assertThat(rightValueProjection1?.double).isEqualTo(rightValueEntity1.double).isEqualTo(1101.1)
            assertThat(rightValueProjection2FromLeftProjection1?.double).isEqualTo(rightValueEntity2.double).isEqualTo(2202.2)
            assertThat(rightValueProjection3?.double).isEqualTo(rightValueEntity3.double).isEqualTo(3303.3)
            assertThat(rightValueProjection1?.leftSet?.size).isEqualTo(1)
            assertThat(rightValueProjection2FromLeftProjection1?.leftSet?.size).isEqualTo(2)
            assertThat(rightValueProjection3?.leftSet?.size).isEqualTo(1)
            assertThat(rightValueProjection1?.leftSet?.map { it.id }).isEqualTo(rightValueEntity1.leftSet?.map { it.id })
            assertThat(rightValueProjection2FromLeftProjection1?.leftSet?.map { it.id!! }?.sorted()).isEqualTo(rightValueEntity2.leftSet?.map { it.id!! }?.sorted())
            assertThat(rightValueProjection3?.leftSet?.map { it.id!! }?.sorted()).isEqualTo(rightValueEntity3.leftSet?.map { it.id!! }?.sorted())
            assertThat(rightKeyProjection2?.int).isEqualTo(rightKeyEntity2.int).isEqualTo(22)
            assertThat(rightKeyProjection3?.int).isEqualTo(rightKeyEntity3.int).isEqualTo(33)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project bidirectional many-to-many map with projected entity key and projected entity value`() {
        val rightKeyEntity1 = MapManyToManyBidirectionalRightKeyEntity().apply {
            int = 11
        }.also { entityManager.persist(it) }

        val rightKeyEntity2 = MapManyToManyBidirectionalRightKeyEntity().apply {
            int = 22
        }.also { entityManager.persist(it) }

        val rightKeyEntity3 = MapManyToManyBidirectionalRightKeyEntity().apply {
            int = 33
        }.also { entityManager.persist(it) }

        val rightValueEntity1 = MapManyToManyBidirectionalRightValueEntityForCaseEntityKey().apply {
            double = 1101.1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapManyToManyBidirectionalRightValueEntityForCaseEntityKey().apply {
            double = 2202.2
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapManyToManyBidirectionalRightValueEntityForCaseEntityKey().apply {
            double = 3303.3
        }.also { entityManager.persist(it) }

        val leftEntity1 = MapManyToManyBidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake1"
            rightMap = mapOf(
                rightKeyEntity1 to rightValueEntity1,
                rightKeyEntity2 to rightValueEntity2
            )
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapManyToManyBidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake2"
            rightMap = mapOf(
                rightKeyEntity2 to rightValueEntity2,
                rightKeyEntity3 to rightValueEntity3
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()

        entityManager.refresh(rightValueEntity1)
        entityManager.refresh(rightValueEntity2)
        entityManager.refresh(rightValueEntity3)

        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapManyToManyBidirectionalLeftEntityWithEntityKey::class,
            MapManyToManyBidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapManyToManyBidirectionalLeftEntityWithEntityKey::id,
                MapManyToManyBidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key.id to it.value.id }?.sortedBy { it.first })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key.id to it.value.id }?.sortedBy { it.first })
                entity.rightMap?.keys?.forEach { key ->
                    val rightKeyProjection = projection.rightMap?.keys?.first { it.id == key.id }
                    val rightValueProjection = projection.rightMap?.get(rightKeyProjection)
                    val rightValueEntity = entity.rightMap?.get(key)
                    assertThat(rightValueProjection?.id).isEqualTo(rightValueEntity?.id)
                    assertThat(rightValueProjection?.double).isEqualTo(rightValueEntity?.double)
                    assertThat(rightValueProjection?.leftSet?.size).isEqualTo(rightValueEntity?.leftSet?.size)
                    assertThat(rightValueProjection?.leftSet?.map { it.id }?.sorted()).isEqualTo(rightValueEntity?.leftSet?.map { it.id!! }?.sorted())
                }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightKeyProjection1 = leftProjection1.rightMap?.keys?.first { it.id == rightKeyEntity1.id }
            val rightKeyProjection2 = leftProjection1.rightMap?.keys?.first { it.id == rightKeyEntity2.id }
            val rightKeyProjection3 = leftProjection2.rightMap?.keys?.first { it.id == rightKeyEntity3.id }

            val rightValueProjection1 = leftProjection1.rightMap?.get(rightKeyProjection1)
            val rightValueProjection2FromLeftProjection1 = leftProjection1.rightMap?.get(rightKeyProjection2)
            val rightValueProjection2FromLeftProjection2 = leftProjection2.rightMap?.get(rightKeyProjection2)
            val rightValueProjection3 = leftProjection2.rightMap?.get(rightKeyProjection3)

            // Assert that projections of the same entity are reused
            assertThat(rightValueProjection2FromLeftProjection1).isSameAs(rightValueProjection2FromLeftProjection2)

            // Assert correct property values in projections
            assertThat(leftProjection1.rightMap?.size).isEqualTo(leftEntity1.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection2.rightMap?.size).isEqualTo(leftEntity2.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection1.string).isEqualTo(leftEntity1.string).isEqualTo("Fake1")
            assertThat(leftProjection2.string).isEqualTo(leftEntity2.string).isEqualTo("Fake2")
            assertThat(rightKeyProjection1?.int).isEqualTo(rightKeyEntity1.int).isEqualTo(11)
            assertThat(rightKeyProjection2?.int).isEqualTo(rightKeyEntity2.int).isEqualTo(22)
            assertThat(rightKeyProjection3?.int).isEqualTo(rightKeyEntity3.int).isEqualTo(33)
            assertThat(rightValueProjection1?.double).isEqualTo(rightValueEntity1.double).isEqualTo(1101.1)
            assertThat(rightValueProjection2FromLeftProjection1?.double).isEqualTo(rightValueEntity2.double).isEqualTo(2202.2)
            assertThat(rightValueProjection3?.double).isEqualTo(rightValueEntity3.double).isEqualTo(3303.3)
            assertThat(rightValueProjection1?.leftSet?.size).isEqualTo(1)
            assertThat(rightValueProjection2FromLeftProjection1?.leftSet?.size).isEqualTo(2)
            assertThat(rightValueProjection3?.leftSet?.size).isEqualTo(1)
            assertThat(rightValueProjection1?.leftSet?.map { it.id }).isEqualTo(rightValueEntity1.leftSet?.map { it.id })
            assertThat(rightValueProjection2FromLeftProjection1?.leftSet?.map { it.id }?.sorted()).isEqualTo(rightValueEntity2.leftSet?.map { it.id!! }?.sorted())
            assertThat(rightValueProjection3?.leftSet?.map { it.id }?.sorted()).isEqualTo(rightValueEntity3.leftSet?.map { it.id!! }?.sorted())
        }

        entityManagerWithCounter.assertQueryCount(3)
    }

}
