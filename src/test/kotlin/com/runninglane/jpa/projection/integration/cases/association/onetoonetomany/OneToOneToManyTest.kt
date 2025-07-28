package com.runninglane.jpa.projection.integration.cases.association.onetoonetomany

import com.runninglane.jpa.projection.integration.BaseTest
import com.runninglane.jpa.projection.integration.EntityManagerWithCounter
import com.runninglane.jpa.projection.mapper.EntityClassMapper
import com.runninglane.jpa.projection.mapper.assert.MapperAssertion
import com.runninglane.jpa.projection.mapper.assert.expectEntityClassMapper
import com.runninglane.jpa.projection.mapper.association.AnyToManyPropertyMapper
import com.runninglane.jpa.projection.mapper.association.AnyToManyPropertyMapperSimplifiedWithJoinFetch
import com.runninglane.jpa.projection.mapper.association.AnyToOnePropertyMapper
import com.runninglane.jpa.projection.mapper.association.AnyToOnePropertyMapperSimplifiedWithJoinFetch
import com.runninglane.jpa.projection.mapper.cases.simplevalue.SimpleValueTest.MockProjectionWithExactTypes
import com.runninglane.jpa.projection.mapper.cases.simplevalue.SimpleValueTest.ProjectionWithExactTypes
import com.runninglane.jpa.projection.mapper.cases.simplevalue.SimpleValueTest.SimpleEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [OneToOneToManyTestConfig::class])
class OneToOneToManyTest : BaseTest() {

    @Test
    fun `should correctly create mapper for one-to-one-to-many`() {
        val leftMapper = EntityClassMapper.of(
            entityManager.projectorFactory,
            LeftEntity::class,
            LeftEntityProjection::class,
            false,
            null
        )

        val middleMapper = leftMapper.getChildren()
            .filterIsInstance<AnyToOnePropertyMapperSimplifiedWithJoinFetch>().first()

        assertThat(middleMapper.propertyName).isEqualTo("middle")
        assertThat(middleMapper.hasJoinFetch()).isTrue()

        val rightMapper = (middleMapper.getChildren().single() as EntityClassMapper).getChildren()
            .filterIsInstance<AnyToManyPropertyMapperSimplifiedWithJoinFetch>().first()

        assertThat(rightMapper.propertyName).isEqualTo("rightList")
        assertThat(rightMapper.hasJoinFetch()).isTrue()



    }

    @Test
    fun `should correctly project one-to-one-to-many`() {
        val rightEntity1 = RightEntity().apply {
            name = "Right1"
        }.also { entityManager.persist(it) }

        val rightEntity2 = RightEntity().apply {
            name = "Right2"
        }.also { entityManager.persist(it) }

        val middleEntity1 = MiddleEntity().apply {
            name = "Middle1"
            rightList = listOf(rightEntity1, rightEntity2)
        }.also { entityManager.persist(it) }

        val leftEntity1 = LeftEntity().apply {
            name = "Left1"
            middle = middleEntity1
        }.also { entityManager.persist(it) }

        val leftEntity2 = LeftEntity().apply {
            name = "Left1"
            middle = middleEntity1
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            LeftEntity::class,
            LeftEntityProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                LeftEntity::id,
                LeftEntityProjection::id
            ) { entity, projection ->
                assertThat(projection.id).isEqualTo(entity.id)
                assertThat(projection.name).isEqualTo(entity.name)
                assertThat(projection.middle!!.id).isEqualTo(entity.middle!!.id)
            }

            val leftProjection1 = projections.first { it.id == leftEntity1.id }
            val leftProjection2 = projections.first { it.id == leftEntity2.id }

            val middleProjectionFromLeft1 = leftProjection1.middle
            val middleProjectionFromLeft2 = leftProjection2.middle

            assertThat(middleProjectionFromLeft1).isSameAs(middleProjectionFromLeft2)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }
}
