package com.runninglane.jpa.projection.integration.cases.association.onetoone

import com.runninglane.jpa.projection.integration.BaseTest
import com.runninglane.jpa.projection.integration.EntityManagerWithCounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [OneToOneTestConfig::class])
class OneToOneTest : BaseTest() {

    @Test
    fun `should correctly project unidirectional one-to-one association`() {
        val leftEntity1 = OneToOneUnidirectionalLeftEntity().apply {
            right = OneToOneUnidirectionalRightEntity().apply {
                int = 1101
                string = "Test1"
            }
        }.also { entityManager.persist(it) }

        val leftEntity2 = OneToOneUnidirectionalLeftEntity().apply {
            right = OneToOneUnidirectionalRightEntity().apply {
                int = 2202
                string = "Test2"
            }
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            OneToOneUnidirectionalLeftEntity::class,
            OneToOneUnidirectionalLeftEntityProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                OneToOneUnidirectionalLeftEntity::id,
                OneToOneUnidirectionalLeftEntityProjection::id
            ) { entity, projection ->
                assertThat(projection.id).isEqualTo(entity.id)
                // Compare all properties with the entity
                assertThat(projection.right!!.id).isEqualTo(entity.right!!.id)
                assertThat(projection.right!!.int).isEqualTo(entity.right!!.int)
                assertThat(projection.right!!.string).isEqualTo(entity.right!!.string)
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightProjection1 = leftProjection1.right
            val rightProjection2 = leftProjection2.right

            // Assert correct property values in projections
            assertThat(leftProjection1.right!!.id).isEqualTo(leftEntity1.right!!.id)
            assertThat(leftProjection2.right!!.id).isEqualTo(leftEntity2.right!!.id)
            assertThat(rightProjection1?.int).isEqualTo(leftEntity1.right!!.int).isEqualTo(1101)
            assertThat(rightProjection1?.string).isEqualTo(leftEntity1.right!!.string).isEqualTo("Test1")
            assertThat(rightProjection2?.int).isEqualTo(leftEntity2.right!!.int).isEqualTo(2202)
            assertThat(rightProjection2?.string).isEqualTo(leftEntity2.right!!.string).isEqualTo("Test2")
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project unidirectional one-to-one association when missing right entity`() {
        OneToOneUnidirectionalLeftEntity().apply {
            right = null
        }.also { entityManager.persist(it) }

        OneToOneUnidirectionalLeftEntity().apply {
            right = null
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerifyEach(
            OneToOneUnidirectionalLeftEntity::class,
            OneToOneUnidirectionalLeftEntityProjection::class,
            OneToOneUnidirectionalLeftEntity::id,
            OneToOneUnidirectionalLeftEntityProjection::id,
            2,
            entityManagerWithCounter
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.right).isNull()
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project bidirectional one-to-one association`() {
        val leftEntity1 = OneToOneBidirectionalLeftEntity().apply {
            right = OneToOneBidirectionalRightEntity().apply {
                int = 1101
                string = "Test1"
            }
        }.also { entityManager.persist(it) }

        val leftEntity2 = OneToOneBidirectionalLeftEntity().apply {
            right = OneToOneBidirectionalRightEntity().apply {
                int = 2202
                string = "Test2"
            }
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            OneToOneBidirectionalLeftEntity::class,
            OneToOneBidirectionalLeftEntityProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                OneToOneBidirectionalLeftEntity::id,
                OneToOneBidirectionalLeftEntityProjection::id,
            ) { entity, projection ->
                assertThat(projection.id).isEqualTo(entity.id)
                // Compare all properties with the entity
                assertThat(projection.right!!.id).isEqualTo(entity.right!!.id)
                assertThat(projection.right!!.int).isEqualTo(entity.right!!.int)
                assertThat(projection.right!!.string).isEqualTo(entity.right!!.string)
                assertThat(projection.right!!.left!!.id).isEqualTo(entity.right!!.left!!.id)
                assertThat(projection.right!!.left).isSameAs(projection)
                assertThat(projection.right!!.left!!.right).isSameAs(projection.right)
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightProjection1 = leftProjection1.right
            val rightProjection2 = leftProjection2.right

            // Assert correct property values in projections
            assertThat(leftProjection1.right!!.id).isEqualTo(leftEntity1.right!!.id)
            assertThat(leftProjection2.right!!.id).isEqualTo(leftEntity2.right!!.id)
            assertThat(rightProjection1?.int).isEqualTo(leftEntity1.right!!.int).isEqualTo(1101)
            assertThat(rightProjection1?.string).isEqualTo(leftEntity1.right!!.string).isEqualTo("Test1")
            assertThat(rightProjection2?.int).isEqualTo(leftEntity2.right!!.int).isEqualTo(2202)
            assertThat(rightProjection2?.string).isEqualTo(leftEntity2.right!!.string).isEqualTo("Test2")
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project bidirectional one-to-one association when missing right entity`() {
        OneToOneBidirectionalLeftEntity().apply {
            right = null
        }.also { entityManager.persist(it) }

        OneToOneBidirectionalLeftEntity().apply {
            right = null
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerifyEach(
            OneToOneBidirectionalLeftEntity::class,
            OneToOneBidirectionalLeftEntityProjection::class,
            OneToOneBidirectionalLeftEntity::id,
            OneToOneBidirectionalLeftEntityProjection::id,
            2,
            entityManagerWithCounter
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.right).isNull()
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project bidirectional one-to-one association from the right side`() {
        val leftEntity1 = OneToOneBidirectionalLeftEntity().apply {
            right = OneToOneBidirectionalRightEntity().apply {
                int = 1101
                string = "Test1"
            }
        }.also { entityManager.persist(it) }

        val leftEntity2 = OneToOneBidirectionalLeftEntity().apply {
            right = OneToOneBidirectionalRightEntity().apply {
                int = 2202
                string = "Test2"
            }
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            OneToOneBidirectionalRightEntity::class,
            OneToOneBidirectionalRightEntityProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                OneToOneBidirectionalRightEntity::id,
                OneToOneBidirectionalRightEntityProjection::id
            ) { entity, projection ->
                assertThat(projection.id).isEqualTo(entity.id)
                // Compare all properties with the entity
                assertThat(projection.left!!.id).isEqualTo(entity.left!!.id)
                assertThat(projection.left!!.right!!.id).isEqualTo(entity.left!!.right!!.id)
                assertThat(projection.left!!.right!!.int).isEqualTo(entity.left!!.right!!.int)
                assertThat(projection.left!!.right!!.string).isEqualTo(entity.left!!.right!!.string)
                assertThat(projection.left!!.right).isSameAs(projection)
                assertThat(projection.left!!.right!!.left).isSameAs(projection.left)
            }

            val rightProjection1 = projections.first { it.id == leftEntity1.right!!.id }
            val rightProjection2 = projections.first { it.id == leftEntity2.right!!.id }

            val leftProjection1 = rightProjection1.left!!
            val leftProjection2 = rightProjection2.left!!

            // Assert correct property values in projections
            assertThat(leftProjection1.right!!.id).isEqualTo(leftEntity1.right!!.id)
            assertThat(leftProjection2.right!!.id).isEqualTo(leftEntity2.right!!.id)
            assertThat(rightProjection1.int).isEqualTo(leftEntity1.right!!.int).isEqualTo(1101)
            assertThat(rightProjection1.string).isEqualTo(leftEntity1.right!!.string).isEqualTo("Test1")
            assertThat(rightProjection2.int).isEqualTo(leftEntity2.right!!.int).isEqualTo(2202)
            assertThat(rightProjection2.string).isEqualTo(leftEntity2.right!!.string).isEqualTo("Test2")
        }

        entityManagerWithCounter.assertQueryCount(1)
    }
}
