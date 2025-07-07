package com.runninglane.jpa.projection.test.cases.association.manytomany.map.unidirectional

import com.runninglane.jpa.projection.test.BaseTest
import com.runninglane.jpa.projection.test.EntityManagerWithCounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [MapManyToManyUnidirectionalTestConfig::class])
class MapManyToManyUnidirectionalTest : BaseTest() {

    @Test
    fun `should correctly project unidirectional many-to-many map with string key and non-projected entity value`() {
        val rightValueEntity1 = MapManyToManyUnidirectionalRightValueEntity().apply {
            double = 1101.1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapManyToManyUnidirectionalRightValueEntity().apply {
            double = 2202.2
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapManyToManyUnidirectionalRightValueEntity().apply {
            double = 3303.3
        }.also { entityManager.persist(it) }

        val leftEntity1 = MapManyToManyUnidirectionalLeftEntityWithStringKey().apply {
            string = "Fake1"
            rightMap = mapOf(
                "Test1" to rightValueEntity1,
                "Test2" to rightValueEntity2
            )
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapManyToManyUnidirectionalLeftEntityWithStringKey().apply {
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
            MapManyToManyUnidirectionalLeftEntityWithStringKey::class,
            MapManyToManyUnidirectionalLeftEntityWithStringKeyProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapManyToManyUnidirectionalLeftEntityWithStringKey::id,
                MapManyToManyUnidirectionalLeftEntityWithStringKeyProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key to it.value.id }?.sortedBy { it.first })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key to it.value.id }?.sortedBy { it.first })
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
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project unidirectional many-to-many map with string key and projected entity value`() {
        val rightValueEntity1 = MapManyToManyUnidirectionalRightValueEntity().apply {
            double = 1101.1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapManyToManyUnidirectionalRightValueEntity().apply {
            double = 2202.2
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapManyToManyUnidirectionalRightValueEntity().apply {
            double = 3303.3
        }.also { entityManager.persist(it) }

        val leftEntity1 = MapManyToManyUnidirectionalLeftEntityWithStringKey().apply {
            string = "Fake1"
            rightMap = mapOf(
                "Test1" to rightValueEntity1,
                "Test2" to rightValueEntity2
            )
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapManyToManyUnidirectionalLeftEntityWithStringKey().apply {
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
            MapManyToManyUnidirectionalLeftEntityWithStringKey::class,
            MapManyToManyUnidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapManyToManyUnidirectionalLeftEntityWithStringKey::id,
                MapManyToManyUnidirectionalLeftEntityWithStringKeyProjectionWithProjectedValue::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key to it.value.id }?.sortedBy { it.first })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key to it.value.id }?.sortedBy { it.first })
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
        }

        entityManagerWithCounter.assertQueryCount(2)
    }

    @Test
    fun `should correctly project unidirectional many-to-many map with non-projected entity key and non-projected entity value`() {
        val rightKeyEntity1 = MapManyToManyUnidirectionalRightKeyEntity().apply {
            int = 11
        }.also { entityManager.persist(it) }

        val rightKeyEntity2 = MapManyToManyUnidirectionalRightKeyEntity().apply {
            int = 22
        }.also { entityManager.persist(it) }

        val rightKeyEntity3 = MapManyToManyUnidirectionalRightKeyEntity().apply {
            int = 33
        }.also { entityManager.persist(it) }

        val rightValueEntity1 = MapManyToManyUnidirectionalRightValueEntity().apply {
            double = 1101.1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapManyToManyUnidirectionalRightValueEntity().apply {
            double = 2202.2
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapManyToManyUnidirectionalRightValueEntity().apply {
            double = 3303.3
        }.also { entityManager.persist(it) }

        val leftEntity1 = MapManyToManyUnidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake1"
            rightMap = mapOf(
                rightKeyEntity1 to rightValueEntity1,
                rightKeyEntity2 to rightValueEntity2
            )
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapManyToManyUnidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake2"
            rightMap = mapOf(
                rightKeyEntity2 to rightValueEntity2,
                rightKeyEntity3 to rightValueEntity3
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapManyToManyUnidirectionalLeftEntityWithEntityKey::class,
            MapManyToManyUnidirectionalLeftEntityWithEntityKeyProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapManyToManyUnidirectionalLeftEntityWithEntityKey::id,
                MapManyToManyUnidirectionalLeftEntityWithEntityKeyProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key.id to it.value.id }?.sortedBy { it.first })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key.id to it.value.id }?.sortedBy { it.first })
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
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project unidirectional many-to-many map with projected entity key and projected entity value`() {
        val rightKeyEntity1 = MapManyToManyUnidirectionalRightKeyEntity().apply {
            int = 11
        }.also { entityManager.persist(it) }

        val rightKeyEntity2 = MapManyToManyUnidirectionalRightKeyEntity().apply {
            int = 22
        }.also { entityManager.persist(it) }

        val rightKeyEntity3 = MapManyToManyUnidirectionalRightKeyEntity().apply {
            int = 33
        }.also { entityManager.persist(it) }

        val rightValueEntity1 = MapManyToManyUnidirectionalRightValueEntity().apply {
            double = 1101.1
        }.also { entityManager.persist(it) }

        val rightValueEntity2 = MapManyToManyUnidirectionalRightValueEntity().apply {
            double = 2202.2
        }.also { entityManager.persist(it) }

        val rightValueEntity3 = MapManyToManyUnidirectionalRightValueEntity().apply {
            double = 3303.3
        }.also { entityManager.persist(it) }

        val leftEntity1 = MapManyToManyUnidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake1"
            rightMap = mapOf(
                rightKeyEntity1 to rightValueEntity1,
                rightKeyEntity2 to rightValueEntity2
            )
        }.also { entityManager.persist(it) }

        val leftEntity2 = MapManyToManyUnidirectionalLeftEntityWithEntityKey().apply {
            string = "Fake2"
            rightMap = mapOf(
                rightKeyEntity2 to rightValueEntity2,
                rightKeyEntity3 to rightValueEntity3
            )
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            MapManyToManyUnidirectionalLeftEntityWithEntityKey::class,
            MapManyToManyUnidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                MapManyToManyUnidirectionalLeftEntityWithEntityKey::id,
                MapManyToManyUnidirectionalLeftEntityWithEntityKeyProjectionWithProjectedKeyAndValue::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightMap?.size).isEqualTo(entity.rightMap?.size)
                assertThat(projection.rightMap?.entries?.map { it.key.id to it.value.id }?.sortedBy { it.first })
                    .isEqualTo(entity.rightMap?.entries?.map { it.key.id to it.value.id }?.sortedBy { it.first })
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
        }

        entityManagerWithCounter.assertQueryCount(2)
    }
}
