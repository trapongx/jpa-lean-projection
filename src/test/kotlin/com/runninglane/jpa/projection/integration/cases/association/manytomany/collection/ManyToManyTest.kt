package com.runninglane.jpa.projection.integration.cases.association.manytomany.collection

import com.runninglane.jpa.projection.integration.BaseTest
import com.runninglane.jpa.projection.integration.EntityManagerWithCounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [ManyToManyTestConfig::class])
class ManyToManyTest : BaseTest() {

    @Test
    fun `should correctly project unidirectional many-to-many association`() {
        val rightEntity1 = ManyToManyUnidirectionalRightEntity().apply {
            int = 1101
            string = "Test1"
        }.also { entityManager.persist(it) }

        val rightEntity2 = ManyToManyUnidirectionalRightEntity().apply {
            int = 2202
            string = "Test2"
        }.also { entityManager.persist(it) }

        val rightEntity3 = ManyToManyUnidirectionalRightEntity().apply {
            int = 3303
            string = "Test3"
        }.also { entityManager.persist(it) }

        val leftEntity1 = ManyToManyUnidirectionalLeftEntity().apply {
            rightList = listOf(rightEntity1, rightEntity2)
        }.also { entityManager.persist(it) }

        val leftEntity2 = ManyToManyUnidirectionalLeftEntity().apply {
            rightList = listOf(rightEntity2, rightEntity3)
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            ManyToManyUnidirectionalLeftEntity::class,
            ManyToManyUnidirectionalLeftEntityProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                ManyToManyUnidirectionalLeftEntity::id,
                ManyToManyUnidirectionalLeftEntityProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightList?.size).isEqualTo(entity.rightList?.size)
                assertThat(projection.rightList?.map { it.id }).isEqualTo(entity.rightList?.map { it.id })
                (projection.rightList?.sortedBy { it.id } ?: emptyList())
                    .zip(entity.rightList?.sortedBy { it.id } ?: emptyList()) { leftProjection, leftEntity ->
                        assertThat(leftProjection.id).isEqualTo(leftEntity.id)
                        assertThat(leftProjection.int).isEqualTo(leftEntity.int)
                        assertThat(leftProjection.string).isEqualTo(leftEntity.string)
                    }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightProjection1 = leftProjection1.rightList?.first { it.id == rightEntity1.id }!!
            val rightProjection2FromLeftProjection1 = leftProjection1.rightList?.first { it.id == rightEntity2.id }!!
            val rightProjection2FromLeftProjection2 = leftProjection2.rightList?.first { it.id == rightEntity2.id }!!
            val rightProjection3 = leftProjection2.rightList?.first { it.id == rightEntity3.id }!!

            // Assert that projections of the same entity are reused
            assertThat(leftProjection1.rightList).isEqualTo(listOf(rightProjection1, rightProjection2FromLeftProjection1))
            assertThat(leftProjection2.rightList).isEqualTo(listOf(rightProjection2FromLeftProjection2, rightProjection3))
            assertThat(rightProjection2FromLeftProjection1).isSameAs(rightProjection2FromLeftProjection2)

            // Assert correct property values in projections
            assertThat(leftProjection1.rightList?.size).isEqualTo(2)
            assertThat(leftProjection2.rightList?.size).isEqualTo(2)
            assertThat(rightProjection1.int).isEqualTo(rightEntity1.int).isEqualTo(1101)
            assertThat(rightProjection1.string).isEqualTo(rightEntity1.string).isEqualTo("Test1")
            assertThat(rightProjection2FromLeftProjection1.int).isEqualTo(rightEntity2.int).isEqualTo(2202)
            assertThat(rightProjection2FromLeftProjection1.string).isEqualTo(rightEntity2.string).isEqualTo("Test2")
            assertThat(rightProjection3.int).isEqualTo(rightEntity3.int).isEqualTo(3303)
            assertThat(rightProjection3.string).isEqualTo(rightEntity3.string).isEqualTo("Test3")
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project bidirectional many-to-many association`() {
        val rightEntity1 = ManyToManyBidirectionalRightEntity().apply {
            int = 1101
            string = "Test1"
        }.also { entityManager.persist(it) }

        val rightEntity2 = ManyToManyBidirectionalRightEntity().apply {
            int = 2202
            string = "Test2"
        }.also { entityManager.persist(it) }

        val rightEntity3 = ManyToManyBidirectionalRightEntity().apply {
            int = 3303
            string = "Test3"
        }.also { entityManager.persist(it) }

        val leftEntity1 = ManyToManyBidirectionalLeftEntity().apply {
            rightList = listOf(rightEntity1, rightEntity2)
        }.also { entityManager.persist(it) }

        val leftEntity2 = ManyToManyBidirectionalLeftEntity().apply {
            rightList = listOf(rightEntity2, rightEntity3)
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            ManyToManyBidirectionalLeftEntity::class,
            ManyToManyBidirectionalLeftEntityProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                ManyToManyBidirectionalLeftEntity::id,
                ManyToManyBidirectionalLeftEntityProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightList?.size).isEqualTo(entity.rightList?.size)
                assertThat(projection.rightList?.map { it.id }).isEqualTo(entity.rightList?.map { it.id })
                (projection.rightList?.sortedBy { it.id } ?: emptyList())
                    .zip(entity.rightList?.sortedBy { it.id } ?: emptyList()) { rightProjection, rightEntity ->
                        assertThat(rightProjection.id).isEqualTo(rightEntity.id)
                        assertThat(rightProjection.int).isEqualTo(rightEntity.int)
                        assertThat(rightProjection.string).isEqualTo(rightEntity.string)
                        assertThat(rightProjection.leftList?.size).isEqualTo(rightEntity.leftList?.size)
                        assertThat(rightProjection.leftList?.map { it.id }).isEqualTo(rightEntity.leftList?.map { it.id })
                    }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightProjection1 = leftProjection1.rightList?.first { it.id == rightEntity1.id }!!
            val rightProjection2FromLeftProjection1 = leftProjection1.rightList?.first { it.id == rightEntity2.id }!!
            val rightProjection2FromLeftProjection2 = leftProjection2.rightList?.first { it.id == rightEntity2.id }!!
            val rightProjection3 = leftProjection2.rightList?.first { it.id == rightEntity3.id }!!

            // Assert that projections of the same entity are reused
            assertThat(leftProjection1.rightList).isEqualTo(listOf(rightProjection1, rightProjection2FromLeftProjection1))
            assertThat(leftProjection2.rightList).isEqualTo(listOf(rightProjection2FromLeftProjection2, rightProjection3))
            assertThat(rightProjection1.leftList).isEqualTo(listOf(leftProjection1))
            assertThat(rightProjection2FromLeftProjection1).isSameAs(rightProjection2FromLeftProjection2)
            assertThat(rightProjection2FromLeftProjection1.leftList).isEqualTo(listOf(leftProjection1, leftProjection2))
            assertThat(rightProjection3.leftList).isEqualTo(listOf(leftProjection2))

            // Assert correct property values in projections
            assertThat(leftProjection1.rightList?.size).isEqualTo(2)
            assertThat(leftProjection2.rightList?.size).isEqualTo(2)
            assertThat(rightProjection1.int).isEqualTo(rightEntity1.int).isEqualTo(1101)
            assertThat(rightProjection1.string).isEqualTo(rightEntity1.string).isEqualTo("Test1")
            assertThat(rightProjection2FromLeftProjection1.int).isEqualTo(rightEntity2.int).isEqualTo(2202)
            assertThat(rightProjection2FromLeftProjection1.string).isEqualTo(rightEntity2.string).isEqualTo("Test2")
            assertThat(rightProjection3.int).isEqualTo(rightEntity3.int).isEqualTo(3303)
            assertThat(rightProjection3.string).isEqualTo(rightEntity3.string).isEqualTo("Test3")
        }

        entityManagerWithCounter.assertQueryCount(2)
    }

    @Test
    fun `should correctly project bidirectional many-to-many association (with where conditions)`() {
        val rightEntity1 = ManyToManyBidirectionalRightEntity().apply {
            int = 1101
            string = "Test1"
        }.also { entityManager.persist(it) }

        val rightEntity2 = ManyToManyBidirectionalRightEntity().apply {
            int = 2202
            string = "Test2"
        }.also { entityManager.persist(it) }

        val rightEntity3 = ManyToManyBidirectionalRightEntity().apply {
            int = 3303
            string = "Test3"
        }.also { entityManager.persist(it) }

        val leftEntity1 = ManyToManyBidirectionalLeftEntity().apply {
            rightList = listOf(rightEntity1, rightEntity2)
        }.also { entityManager.persist(it) }

        val leftEntity2 = ManyToManyBidirectionalLeftEntity().apply {
            rightList = listOf(rightEntity2, rightEntity3)
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            ManyToManyBidirectionalLeftEntity::class,
            ManyToManyBidirectionalLeftEntityProjection::class,
            1,
            entityManagerWithCounter,
            predicateBuilder = { cb, _, root ->
                cb.equal(root.get<Any>(ManyToManyBidirectionalLeftEntity::id.name), leftEntity1.id)
            }
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                ManyToManyBidirectionalLeftEntity::id,
                ManyToManyBidirectionalLeftEntityProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightList?.size).isEqualTo(entity.rightList?.size)
                assertThat(projection.rightList?.map { it.id }).isEqualTo(entity.rightList?.map { it.id })
                (projection.rightList?.sortedBy { it.id } ?: emptyList())
                    .zip(entity.rightList?.sortedBy { it.id } ?: emptyList()) { rightProjection, rightEntity ->
                        assertThat(rightProjection.id).isEqualTo(rightEntity.id)
                        assertThat(rightProjection.int).isEqualTo(rightEntity.int)
                        assertThat(rightProjection.string).isEqualTo(rightEntity.string)
                        assertThat(rightProjection.leftList?.size).isEqualTo(rightEntity.leftList?.size)
                        assertThat(rightProjection.leftList?.map { it.id }).isEqualTo(rightEntity.leftList?.map { it.id })
                    }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }

            val rightProjection1 = leftProjection1.rightList?.first { it.id == rightEntity1.id }!!
            val rightProjection2 = leftProjection1.rightList?.first { it.id == rightEntity2.id }!!

            val leftProjection2 = rightProjection2.leftList?.first { it.id == leftEntity2.id }!!

            val rightProjection3 = leftProjection2.rightList?.first { it.id == rightEntity3.id }!!

            // Assert that projections of the same entity are reused
            assertThat(leftProjection1.rightList).isEqualTo(listOf(rightProjection1, rightProjection2))
            assertThat(leftProjection2.rightList).isEqualTo(listOf(rightProjection2, rightProjection3))
            assertThat(leftProjection1.rightList?.first { it.id == rightEntity2.id })
                .isSameAs(leftProjection2.rightList?.first { it.id == rightEntity2.id })
            assertThat(rightProjection1.leftList).isEqualTo(listOf(leftProjection1))
            assertThat(rightProjection2.leftList).isEqualTo(listOf(leftProjection1, leftProjection2))
            assertThat(rightProjection3.leftList).isEqualTo(listOf(leftProjection2))

            // Assert correct property values in projections
            assertThat(leftProjection1.rightList?.size).isEqualTo(2)
            assertThat(leftProjection2.rightList?.size).isEqualTo(2)
            assertThat(rightProjection1.int).isEqualTo(rightEntity1.int).isEqualTo(1101)
            assertThat(rightProjection1.string).isEqualTo(rightEntity1.string).isEqualTo("Test1")
            assertThat(rightProjection2.int).isEqualTo(rightEntity2.int).isEqualTo(2202)
            assertThat(rightProjection2.string).isEqualTo(rightEntity2.string).isEqualTo("Test2")
            assertThat(rightProjection3.int).isEqualTo(rightEntity3.int).isEqualTo(3303)
            assertThat(rightProjection3.string).isEqualTo(rightEntity3.string).isEqualTo("Test3")
        }

        entityManagerWithCounter.assertQueryCount(4)
    }

    @Test
    fun `should correctly project bidirectional many-to-many association (with where conditions) (query from right side)`() {
        val rightEntity1 = ManyToManyBidirectionalRightEntity().apply {
            int = 1101
            string = "Test1"
        }.also { entityManager.persist(it) }

        val rightEntity2 = ManyToManyBidirectionalRightEntity().apply {
            int = 2202
            string = "Test2"
        }.also { entityManager.persist(it) }

        val rightEntity3 = ManyToManyBidirectionalRightEntity().apply {
            int = 3303
            string = "Test3"
        }.also { entityManager.persist(it) }

        val leftEntity1 = ManyToManyBidirectionalLeftEntity().apply {
            rightList = listOf(rightEntity1, rightEntity2)
        }.also { entityManager.persist(it) }

        val leftEntity2 = ManyToManyBidirectionalLeftEntity().apply {
            rightList = listOf(rightEntity2, rightEntity3)
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            ManyToManyBidirectionalRightEntity::class,
            ManyToManyBidirectionalRightEntityProjection::class,
            1,
            entityManagerWithCounter,
            predicateBuilder = { cb, _, root ->
                cb.equal(root.get<Any>(ManyToManyBidirectionalRightEntity::id.name), rightEntity2.id)
            }
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                ManyToManyBidirectionalRightEntity::id,
                ManyToManyBidirectionalRightEntityProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.leftList?.size).isEqualTo(entity.leftList?.size)
                assertThat(projection.leftList?.map { it.id }).isEqualTo(entity.leftList?.map { it.id })
                assertThat(projection.int).isEqualTo(entity.int)
                assertThat(projection.string).isEqualTo(entity.string)
                (projection.leftList?.sortedBy { it.id } ?: emptyList())
                    .zip(entity.leftList?.sortedBy { it.id } ?: emptyList()) { leftProjection, leftEntity ->
                        assertThat(leftProjection.id).isEqualTo(leftEntity.id)
                        assertThat(leftProjection.rightList?.size).isEqualTo(leftEntity.rightList?.size)
                        assertThat(leftProjection.rightList?.map { it.id }).isEqualTo(leftEntity.rightList?.map { it.id })
                    }
            }

            val rightProjection2 = projections.first { it.id == rightEntity2.id }

            val leftProjection1 = rightProjection2.leftList?.first { it.id == leftEntity1.id }!!
            val leftProjection2 = rightProjection2.leftList?.first { it.id == leftEntity2.id }!!

            val rightProjection1 = leftProjection1.rightList?.first { it.id == rightEntity1.id }!!
            val rightProjection3 = leftProjection2.rightList?.first { it.id == rightEntity3.id }!!

            // Assert that projections of the same entity are reused
            assertThat(leftProjection1.rightList).isEqualTo(listOf(rightProjection1, rightProjection2))
            assertThat(leftProjection2.rightList).isEqualTo(listOf(rightProjection2, rightProjection3))
            assertThat(leftProjection1.rightList?.first { it.id == rightEntity2.id })
                .isSameAs(leftProjection2.rightList?.first { it.id == rightEntity2.id })
            assertThat(rightProjection1.leftList).isEqualTo(listOf(leftProjection1))
            assertThat(rightProjection2.leftList).isEqualTo(listOf(leftProjection1, leftProjection2))
            assertThat(rightProjection3.leftList).isEqualTo(listOf(leftProjection2))

            // Assert correct property values in projections
            assertThat(leftProjection1.rightList?.size).isEqualTo(2)
            assertThat(leftProjection2.rightList?.size).isEqualTo(2)
            assertThat(rightProjection1.int).isEqualTo(rightEntity1.int).isEqualTo(1101)
            assertThat(rightProjection1.string).isEqualTo(rightEntity1.string).isEqualTo("Test1")
            assertThat(rightProjection2.int).isEqualTo(rightEntity2.int).isEqualTo(2202)
            assertThat(rightProjection2.string).isEqualTo(rightEntity2.string).isEqualTo("Test2")
            assertThat(rightProjection3.int).isEqualTo(rightEntity3.int).isEqualTo(3303)
            assertThat(rightProjection3.string).isEqualTo(rightEntity3.string).isEqualTo("Test3")
        }

        entityManagerWithCounter.assertQueryCount(3)
    }
}
