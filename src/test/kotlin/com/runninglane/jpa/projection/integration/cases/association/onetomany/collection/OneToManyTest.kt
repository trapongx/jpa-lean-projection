package com.runninglane.jpa.projection.integration.cases.association.onetomany.collection

import com.runninglane.jpa.projection.integration.BaseTest
import com.runninglane.jpa.projection.integration.EntityManagerWithCounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [OneToManyTestConfig::class])
class OneToManyTest : BaseTest() {

    @Test
    fun `should correctly project unidirectional one-to-many association`() {
        val rightEntity1 = OneToManyUnidirectionalRightEntity().apply {
            int = 1101
            string = "Test1"
        }.also { entityManager.persist(it) }

        val rightEntity2 = OneToManyUnidirectionalRightEntity().apply {
            int = 2202
            string = "Test2"
        }.also { entityManager.persist(it) }

        val rightEntity3 = OneToManyUnidirectionalRightEntity().apply {
            int = 3303
            string = "Test3"
        }.also { entityManager.persist(it) }

        val leftEntity1 = OneToManyUnidirectionalLeftEntity().apply {
            rightList = listOf(rightEntity1, rightEntity2)
        }.also { entityManager.persist(it) }

        val leftEntity2 = OneToManyUnidirectionalLeftEntity().apply {
            rightList = listOf(rightEntity3)
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            OneToManyUnidirectionalLeftEntity::class,
            OneToManyUnidirectionalLeftEntityProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                OneToManyUnidirectionalLeftEntity::id,
                OneToManyUnidirectionalLeftEntityProjection::id
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
            val rightProjection2 = leftProjection1.rightList?.first { it.id == rightEntity2.id }!!
            val rightProjection3 = leftProjection2.rightList?.first { it.id == rightEntity3.id }!!

            // Assert that projections of the same entity are reused
            assertThat(leftProjection1.rightList).isEqualTo(listOf(rightProjection1, rightProjection2))
            assertThat(leftProjection2.rightList).isEqualTo(listOf(rightProjection3))

            // Assert correct property values in projections
            assertThat(leftProjection1.rightList?.size).isEqualTo(2)
            assertThat(leftProjection2.rightList?.size).isEqualTo(1)
            assertThat(rightProjection1.int).isEqualTo(rightEntity1.int).isEqualTo(1101)
            assertThat(rightProjection1.string).isEqualTo(rightEntity1.string).isEqualTo("Test1")
            assertThat(rightProjection2.int).isEqualTo(rightEntity2.int).isEqualTo(2202)
            assertThat(rightProjection2.string).isEqualTo(rightEntity2.string).isEqualTo("Test2")
            assertThat(rightProjection3.int).isEqualTo(rightEntity3.int).isEqualTo(3303)
            assertThat(rightProjection3.string).isEqualTo(rightEntity3.string).isEqualTo("Test3")
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should correctly project bidirectional one-to-many association`() {
        val leftEntity1 = OneToManyBidirectionalLeftEntity()
            .also { entityManager.persist(it) }

        val leftEntity2 = OneToManyBidirectionalLeftEntity()
            .also { entityManager.persist(it) }

        val rightEntity1 = OneToManyBidirectionalRightEntity().apply {
            int = 1101
            string = "Test1"
            left = leftEntity1
        }.also { entityManager.persist(it) }

        val rightEntity2 = OneToManyBidirectionalRightEntity().apply {
            int = 2202
            string = "Test2"
            left = leftEntity1
        }.also { entityManager.persist(it) }

        val rightEntity3 = OneToManyBidirectionalRightEntity().apply {
            int = 3303
            string = "Test3"
            left = leftEntity2
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            OneToManyBidirectionalLeftEntity::class,
            OneToManyBidirectionalLeftEntityProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                OneToManyBidirectionalLeftEntity::id,
                OneToManyBidirectionalLeftEntityProjection::id
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.rightList?.size).isEqualTo(entity.rightList?.size)
                assertThat(projection.rightList?.map { it.id }).isEqualTo(entity.rightList?.map { it.id })
                (projection.rightList?.sortedBy { it.id } ?: emptyList())
                    .zip(entity.rightList?.sortedBy { it.id } ?: emptyList()) { rightProjection, rightEntity ->
                        assertThat(rightProjection.id).isEqualTo(rightEntity.id)
                        assertThat(rightProjection.int).isEqualTo(rightEntity.int)
                        assertThat(rightProjection.string).isEqualTo(rightEntity.string)
                        assertThat(rightProjection.left?.id).isEqualTo(rightEntity.left?.id)
                    }
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val rightProjection1 = leftProjection1.rightList?.first { it.id == rightEntity1.id }!!
            val rightProjection2 = leftProjection1.rightList?.first { it.id == rightEntity2.id }!!
            val rightProjection3 = leftProjection2.rightList?.first { it.id == rightEntity3.id }!!

            // Assert that projections of the same entity are reused
            assertThat(leftProjection1.rightList).isEqualTo(listOf(rightProjection1, rightProjection2))
            assertThat(leftProjection2.rightList).isEqualTo(listOf(rightProjection3))
            assertThat(rightProjection1.left).isSameAs(leftProjection1)
            assertThat(rightProjection2.left).isSameAs(leftProjection1)
            assertThat(rightProjection3.left).isSameAs(leftProjection2)

            // Assert correct property values in projections
            assertThat(leftProjection1.rightList?.size).isEqualTo(2)
            assertThat(leftProjection2.rightList?.size).isEqualTo(1)
            assertThat(rightProjection1.int).isEqualTo(rightEntity1.int).isEqualTo(1101)
            assertThat(rightProjection1.string).isEqualTo(rightEntity1.string).isEqualTo("Test1")
            assertThat(rightProjection1.left?.id).isEqualTo(rightEntity1.left?.id).isEqualTo(leftEntity1.id)
            assertThat(rightProjection2.int).isEqualTo(rightEntity2.int).isEqualTo(2202)
            assertThat(rightProjection2.string).isEqualTo(rightEntity2.string).isEqualTo("Test2")
            assertThat(rightProjection2.left?.id).isEqualTo(rightEntity2.left?.id).isEqualTo(leftEntity1.id)
            assertThat(rightProjection3.int).isEqualTo(rightEntity3.int).isEqualTo(3303)
            assertThat(rightProjection3.string).isEqualTo(rightEntity3.string).isEqualTo("Test3")
            assertThat(rightProjection3.left?.id).isEqualTo(rightEntity3.left?.id).isEqualTo(leftEntity2.id)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

}
