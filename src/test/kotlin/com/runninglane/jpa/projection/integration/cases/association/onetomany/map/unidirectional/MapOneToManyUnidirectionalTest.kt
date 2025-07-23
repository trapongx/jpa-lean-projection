package com.runninglane.jpa.projection.integration.cases.association.onetomany.map.unidirectional

import com.runninglane.jpa.projection.integration.BaseTest
import com.runninglane.jpa.projection.integration.EntityManagerWithCounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [MapOneToManyUnidirectionalTestConfig::class])
class MapOneToManyUnidirectionalTest : BaseTest() {

    @Test
    fun `should correctly project unidirectional one-to-many map with string key and non-projected entity value`() {
        val rightValueEntity1 = MapOneToManyUnidirectionalRightValueEntity().apply {
            double = 1101.1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapOneToManyUnidirectionalRightValueEntity().apply {
            double = 2202.2
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapOneToManyUnidirectionalRightValueEntity().apply {
            double = 3303.3
        }.also { entityManager.persist(it) }

        val leftEntity1 = MapOneToManyUnidirectionalLeftEntityWithStringKey().apply {
            string = "Fake1"
            rightMap = mapOf(
                "Test1" to rightValueEntity1,
                "Test2" to rightValueEntity2
            )
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapOneToManyUnidirectionalLeftEntityWithStringKey().apply {
            string = "Fake2"
            rightMap = mapOf(
                "Test3" to rightValueEntity3
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapOneToManyUnidirectionalLeftEntityWithStringKey::class,
            MapOneToManyUnidirectionalLeftEntityWithStringKeyProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapOneToManyUnidirectionalLeftEntityWithStringKey::id,
                MapOneToManyUnidirectionalLeftEntityWithStringKeyProjection::id
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
                }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightValueProjection1 = leftProjection1.rightMap?.get("Test1")
            val rightValueProjection2 = leftProjection1.rightMap?.get("Test2")
            val rightValueProjection3 = leftProjection2.rightMap?.get("Test3")

            // Assert correct property values in projections
            assertThat(leftProjection1.rightMap?.size).isEqualTo(leftEntity1.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection2.rightMap?.size).isEqualTo(leftEntity2.rightMap?.size).isEqualTo(1)
            assertThat(leftProjection1.string).isEqualTo(leftEntity1.string).isEqualTo("Fake1")
            assertThat(leftProjection2.string).isEqualTo(leftEntity2.string).isEqualTo("Fake2")
            assertThat(rightValueProjection1?.double).isEqualTo(rightValueEntity1.double).isEqualTo(1101.1)
            assertThat(rightValueProjection2?.double).isEqualTo(rightValueEntity2.double).isEqualTo(2202.2)
            assertThat(rightValueProjection3?.double).isEqualTo(rightValueEntity3.double).isEqualTo(3303.3)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project unidirectional one-to-many map with string key and projected entity value`() {
        val rightValueEntity1 = MapOneToManyUnidirectionalRightValueEntity().apply {
            double = 1101.1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapOneToManyUnidirectionalRightValueEntity().apply {
            double = 2202.2
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapOneToManyUnidirectionalRightValueEntity().apply {
            double = 3303.3
        }.also { entityManager.persist(it) }

        val leftEntity1 = MapOneToManyUnidirectionalLeftEntityWithStringKey().apply {
            string = "Fake1"
            rightMap = mapOf(
                "Test1" to rightValueEntity1,
                "Test2" to rightValueEntity2
            )
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapOneToManyUnidirectionalLeftEntityWithStringKey().apply {
            string = "Fake2"
            rightMap = mapOf(
                "Test3" to rightValueEntity3
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapOneToManyUnidirectionalLeftEntityWithStringKey::class,
            MapOneToManyUnidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapOneToManyUnidirectionalLeftEntityWithStringKey::id,
                MapOneToManyUnidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue::id
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
                }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightValueProjection1 = leftProjection1.rightMap?.get("Test1")
            val rightValueProjection2 = leftProjection1.rightMap?.get("Test2")
            val rightValueProjection3 = leftProjection2.rightMap?.get("Test3")

            // Assert correct property values in projections
            assertThat(leftProjection1.rightMap?.size).isEqualTo(leftEntity1.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection2.rightMap?.size).isEqualTo(leftEntity2.rightMap?.size).isEqualTo(1)
            assertThat(leftProjection1.string).isEqualTo(leftEntity1.string).isEqualTo("Fake1")
            assertThat(leftProjection2.string).isEqualTo(leftEntity2.string).isEqualTo("Fake2")
            assertThat(rightValueProjection1?.double).isEqualTo(rightValueEntity1.double).isEqualTo(1101.1)
            assertThat(rightValueProjection2?.double).isEqualTo(rightValueEntity2.double).isEqualTo(2202.2)
            assertThat(rightValueProjection3?.double).isEqualTo(rightValueEntity3.double).isEqualTo(3303.3)
        }

        entityManagerWithCounter.assertQueryCount(2)
    }

    @Test
    fun `should correctly project unidirectional one-to-many map with non-projected entity key and non-projected entity value`() {
        val rightKeyEntity1 = MapOneToManyUnidirectionalRightKeyEntity().apply {
            int = 11
        }.also { entityManager.persist(it) }

        val rightKeyEntity2 = MapOneToManyUnidirectionalRightKeyEntity().apply {
            int = 22
        }.also { entityManager.persist(it) }

        val rightKeyEntity3 = MapOneToManyUnidirectionalRightKeyEntity().apply {
            int = 33
        }.also { entityManager.persist(it) }

        val rightValueEntity1 = MapOneToManyUnidirectionalRightValueEntity().apply {
            double = 1101.1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapOneToManyUnidirectionalRightValueEntity().apply {
            double = 2202.2
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapOneToManyUnidirectionalRightValueEntity().apply {
            double = 3303.3
        }.also { entityManager.persist(it) }

        val leftEntity1 = MapOneToManyUnidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake1"
            rightMap = mapOf(
                rightKeyEntity1 to rightValueEntity1,
                rightKeyEntity2 to rightValueEntity2
            )
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapOneToManyUnidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake2"
            rightMap = mapOf(
                rightKeyEntity3 to rightValueEntity3
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapOneToManyUnidirectionalLeftEntityWithEntityKey::class,
            MapOneToManyUnidirectionalLeftEntityWithEntityKeyProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapOneToManyUnidirectionalLeftEntityWithEntityKey::id,
                MapOneToManyUnidirectionalLeftEntityWithEntityKeyProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key.id to it.value.id })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key.id to it.value.id })
                entity.rightMap?.keys?.forEach { key ->
                    val rightValueProjection = projection.rightMap?.get(key)!!
                    val rightValueEntity = entity.rightMap?.get(key)!!
                    assertThat(rightValueProjection.id).isEqualTo(rightValueEntity.id)
                    assertThat(rightValueProjection.double).isEqualTo(rightValueEntity.double)
                }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightKeyProjection1 = leftProjection1.rightMap?.keys?.first { it.id == rightKeyEntity1.id }
            val rightKeyProjection2 = leftProjection1.rightMap?.keys?.first { it.id == rightKeyEntity2.id }
            val rightKeyProjection3 = leftProjection2.rightMap?.keys?.first { it.id == rightKeyEntity3.id }

            val rightValueProjection1 = leftProjection1.rightMap?.get(rightKeyProjection1)
            val rightValueProjection2 = leftProjection1.rightMap?.get(rightKeyProjection2)
            val rightValueProjection3 = leftProjection2.rightMap?.get(rightKeyProjection3)

            // Assert correct property values in projections
            assertThat(leftProjection1.rightMap?.size).isEqualTo(leftEntity1.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection2.rightMap?.size).isEqualTo(leftEntity2.rightMap?.size).isEqualTo(1)
            assertThat(leftProjection1.string).isEqualTo(leftEntity1.string).isEqualTo("Fake1")
            assertThat(leftProjection2.string).isEqualTo(leftEntity2.string).isEqualTo("Fake2")
            assertThat(rightKeyProjection1?.int).isEqualTo(rightKeyEntity1.int).isEqualTo(11)
            assertThat(rightKeyProjection2?.int).isEqualTo(rightKeyEntity2.int).isEqualTo(22)
            assertThat(rightKeyProjection3?.int).isEqualTo(rightKeyEntity3.int).isEqualTo(33)
            assertThat(rightValueProjection1?.double).isEqualTo(rightValueEntity1.double).isEqualTo(1101.1)
            assertThat(rightValueProjection2?.double).isEqualTo(rightValueEntity2.double).isEqualTo(2202.2)
            assertThat(rightValueProjection3?.double).isEqualTo(rightValueEntity3.double).isEqualTo(3303.3)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project unidirectional one-to-many map with projected entity key and projected entity value`() {
        val rightKeyEntity1 = MapOneToManyUnidirectionalRightKeyEntity().apply {
            int = 11
        }.also { entityManager.persist(it) }

        val rightKeyEntity2 = MapOneToManyUnidirectionalRightKeyEntity().apply {
            int = 22
        }.also { entityManager.persist(it) }

        val rightKeyEntity3 = MapOneToManyUnidirectionalRightKeyEntity().apply {
            int = 33
        }.also { entityManager.persist(it) }

        val rightValueEntity1 = MapOneToManyUnidirectionalRightValueEntity().apply {
            double = 1101.1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapOneToManyUnidirectionalRightValueEntity().apply {
            double = 2202.2
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapOneToManyUnidirectionalRightValueEntity().apply {
            double = 3303.3
        }.also { entityManager.persist(it) }

        val leftEntity1 = MapOneToManyUnidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake1"
            rightMap = mapOf(
                rightKeyEntity1 to rightValueEntity1,
                rightKeyEntity2 to rightValueEntity2
            )
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapOneToManyUnidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake2"
            rightMap = mapOf(
                rightKeyEntity3 to rightValueEntity3
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapOneToManyUnidirectionalLeftEntityWithEntityKey::class,
            MapOneToManyUnidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapOneToManyUnidirectionalLeftEntityWithEntityKey::id,
                MapOneToManyUnidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key.id to it.value.id })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key.id to it.value.id })
                entity.rightMap?.keys?.forEach { key ->
                    val rightKeyProjection = projection.rightMap?.keys?.first { it.id == key.id }!!
                    val rightValueProjection = projection.rightMap?.get(rightKeyProjection)!!
                    val rightValueEntity = entity.rightMap?.get(key)!!
                    assertThat(rightValueProjection.id).isEqualTo(rightValueEntity.id)
                    assertThat(rightValueProjection.double).isEqualTo(rightValueEntity.double)
                }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightKeyProjection1 = leftProjection1.rightMap?.keys?.first { it.id == rightKeyEntity1.id }
            val rightKeyProjection2 = leftProjection1.rightMap?.keys?.first { it.id == rightKeyEntity2.id }
            val rightKeyProjection3 = leftProjection2.rightMap?.keys?.first { it.id == rightKeyEntity3.id }

            val rightValueProjection1 = leftProjection1.rightMap?.get(rightKeyProjection1)
            val rightValueProjection2 = leftProjection1.rightMap?.get(rightKeyProjection2)
            val rightValueProjection3 = leftProjection2.rightMap?.get(rightKeyProjection3)

            // Assert correct property values in projections
            assertThat(leftProjection1.rightMap?.size).isEqualTo(leftEntity1.rightMap?.size).isEqualTo(2)
            assertThat(leftProjection2.rightMap?.size).isEqualTo(leftEntity2.rightMap?.size).isEqualTo(1)
            assertThat(leftProjection1.string).isEqualTo(leftEntity1.string).isEqualTo("Fake1")
            assertThat(leftProjection2.string).isEqualTo(leftEntity2.string).isEqualTo("Fake2")
            assertThat(rightKeyProjection1?.int).isEqualTo(rightKeyEntity1.int).isEqualTo(11)
            assertThat(rightKeyProjection2?.int).isEqualTo(rightKeyEntity2.int).isEqualTo(22)
            assertThat(rightKeyProjection3?.int).isEqualTo(rightKeyEntity3.int).isEqualTo(33)
            assertThat(rightValueProjection1?.double).isEqualTo(rightValueEntity1.double).isEqualTo(1101.1)
            assertThat(rightValueProjection2?.double).isEqualTo(rightValueEntity2.double).isEqualTo(2202.2)
            assertThat(rightValueProjection3?.double).isEqualTo(rightValueEntity3.double).isEqualTo(3303.3)
        }

        entityManagerWithCounter.assertQueryCount(2)
    }
}
