package com.runninglane.jpa.projection.test.cases.association.manytoone

import com.runninglane.jpa.projection.test.BaseTest
import com.runninglane.jpa.projection.test.EntityManagerWithCounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [ManyToOneTestConfig::class])
class ManyToOneTest : BaseTest() {

    @Test
    fun `should correctly project unidirectional many-to-one association`() {
        val rightEntity1 = ManyToOneUnidirectionalRightEntity().apply {
            int = 1101
            string = "Test1"
        }.also { entityManager.persist(it) }

        val rightEntity2 = ManyToOneUnidirectionalRightEntity().apply {
            int = 2202
            string = "Test2"
        }.also { entityManager.persist(it) }

        val leftEntity1 = ManyToOneUnidirectionalLeftEntity().apply {
            right = rightEntity1
        }.also { entityManager.persist(it) }

        val leftEntity2 = ManyToOneUnidirectionalLeftEntity().apply {
            right = rightEntity1
        }.also { entityManager.persist(it) }

        val leftEntity3 = ManyToOneUnidirectionalLeftEntity().apply {
            right = rightEntity2
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            ManyToOneUnidirectionalLeftEntity::class,
            ManyToOneUnidirectionalLeftEntityProjection::class,
            3,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                ManyToOneUnidirectionalLeftEntity::id,
                ManyToOneUnidirectionalLeftEntityProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.right!!.id).isEqualTo(entity.right!!.id)
                assertThat(projection.right!!.int).isEqualTo(entity.right!!.int)
                assertThat(projection.right!!.string).isEqualTo(entity.right!!.string)
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }
            val leftProjection3 = projections.first { it.id == leftEntity3.id }

            val rightProjection1FromLeftProjection1 = leftProjection1.right
            val rightProjection1FromLeftProjection2 = leftProjection2.right
            val rightProjection2 = leftProjection3.right

            // Assert that projections of the same entity are reused
            assertThat(rightProjection1FromLeftProjection1).isSameAs(rightProjection1FromLeftProjection2)
            assertThat(rightProjection1FromLeftProjection1?.id).isEqualTo(rightEntity1.id)
            assertThat(rightProjection2?.id).isEqualTo(rightEntity2.id)

            // Assert that simple values are properly set
            assertThat(rightProjection1FromLeftProjection1?.int).isEqualTo(rightEntity1.int).isEqualTo(1101)
            assertThat(rightProjection1FromLeftProjection1?.string).isEqualTo(rightEntity1.string).isEqualTo("Test1")
            assertThat(rightProjection2?.int).isEqualTo(rightEntity2.int).isEqualTo(2202)
            assertThat(rightProjection2?.string).isEqualTo(rightEntity2.string).isEqualTo("Test2")
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project bidirectional many-to-one association`() {
        val rightEntity1 = ManyToOneBidirectionalRightEntity().apply {
            int = 1101
            string = "Test1"
        }.also { entityManager.persist(it) }

        val rightEntity2 = ManyToOneBidirectionalRightEntity().apply {
            int = 2202
            string = "Test2"
        }.also { entityManager.persist(it) }

        val leftEntity1 = ManyToOneBidirectionalLeftEntity().apply {
            right = rightEntity1
        }.also { entityManager.persist(it) }

        val leftEntity2 = ManyToOneBidirectionalLeftEntity().apply {
            right = rightEntity1
        }.also { entityManager.persist(it) }

        val leftEntity3 = ManyToOneBidirectionalLeftEntity().apply {
            right = rightEntity2
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            ManyToOneBidirectionalLeftEntity::class,
            ManyToOneBidirectionalLeftEntityProjection::class,
            3,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                ManyToOneBidirectionalLeftEntity::id,
                ManyToOneBidirectionalLeftEntityProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.right!!.id).isEqualTo(entity.right!!.id)
                assertThat(projection.right!!.int).isEqualTo(entity.right!!.int)
                assertThat(projection.right!!.string).isEqualTo(entity.right!!.string)
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }
            val leftProjection3 = projections.first { it.id == leftEntity3.id }

            val rightProjections = projections.map { it.right!! }
            val rightProjection1 = rightProjections.first { it.id == rightEntity1.id }
            val rightProjection2 = rightProjections.first { it.id == rightEntity2.id }

            // Assert that projections of the same entity are reused
            assertThat(leftProjection1.right?.id).isEqualTo(rightEntity1.id)
            assertThat(leftProjection2.right?.id).isEqualTo(rightEntity1.id)
            assertThat(leftProjection1.right).isSameAs(leftProjection2.right)
            assertThat(leftProjection3.right?.id).isEqualTo(rightEntity2.id)
            assertThat(rightProjection1.leftList).hasSize(2)
            assertThat(rightProjection1.leftList?.first { it.id == leftProjection1.id }).isSameAs(leftProjection1)
            assertThat(rightProjection1.leftList?.first { it.id == leftProjection2.id }).isSameAs(leftProjection2)
            assertThat(rightProjection2.leftList).hasSize(1)
            assertThat(rightProjection2.leftList?.first { it.id == leftProjection3.id }).isSameAs(leftProjection3)

            // Assert that simple values are properly set
            assertThat(rightProjection1.int).isEqualTo(rightEntity1.int).isEqualTo(1101)
            assertThat(rightProjection1.string).isEqualTo(rightEntity1.string).isEqualTo("Test1")
            assertThat(rightProjection2.int).isEqualTo(rightEntity2.int).isEqualTo(2202)
            assertThat(rightProjection2.string).isEqualTo(rightEntity2.string).isEqualTo("Test2")
        }

        entityManagerWithCounter.assertQueryCount(2)
    }

}
