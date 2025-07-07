package com.runninglane.jpa.projection.test.cases.simplevalue

import com.runninglane.jpa.projection.test.BaseTest
import com.runninglane.jpa.projection.test.cases.simplevalue.SimpleValueTest.InnerDepth1.ProjectionAsInnerOfInnerInterface
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [SimpleValueTestConfig::class])
class SimpleValueTest : BaseTest() {

    interface ProjectionAsInnerInterface {
        val id: Long
        var int: Int?
        var long: Long
    }

    interface InnerDepth1 {
        interface ProjectionAsInnerOfInnerInterface {
            val id: Long
            var int: Int?
            var long: Long
        }
    }

    private fun createDefaultTestEntities() {
        // Create test entities
        val entity1 = EntityWithSimpleValues()
        entity1.apply {
            boolean = true
            byte = 1
            short = 2
            char = 'A'
            int = 100
            long = 200L
            float = 1.5f
            double = 2.5
            string = "Test1"
            date = java.util.Date()
            localDate = java.time.LocalDate.now()
            bigInteger = java.math.BigInteger.valueOf(300)
            bigDecimal = java.math.BigDecimal("400.5")
            blob = byteArrayOf(1, 2, 3)
            clob = "Test CLOB 1"
            enum = TestEnum.ONE
            uuid = java.util.UUID.randomUUID()
            duration = java.time.Duration.ofHours(1)
            instant = java.time.Instant.now()
        }

        val entity2 = EntityWithSimpleValues()
        entity2.apply {
            boolean = false
            byte = 2
            short = 3
            char = 'B'
            int = 101
            long = 201L
            float = 1.6f
            double = 2.6
            string = "Test2"
            date = java.util.Date()
            localDate = java.time.LocalDate.now()
            bigInteger = java.math.BigInteger.valueOf(301)
            bigDecimal = java.math.BigDecimal("401.5")
            blob = byteArrayOf(4, 5, 6)
            clob = "Test CLOB 2"
            enum = TestEnum.TWO
            uuid = java.util.UUID.randomUUID()
            duration = java.time.Duration.ofHours(2)
            instant = java.time.Instant.now()
        }

        entityManager.persist(entity1)
        entityManager.persist(entity2)
        entityManager.flush()
    }

    @Test
    fun `should correctly project simple properties`() {
        createDefaultTestEntities()

        projectAndVerifyEach(
            EntityWithSimpleValues::class,
            ProjectionWithExactTypes::class,
            EntityWithSimpleValues::id,
            ProjectionWithExactTypes::id,
            2
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.boolean).isEqualTo(entity.boolean)
            assertThat(projection.byte).isEqualTo(entity.byte)
            assertThat(projection.short).isEqualTo(entity.short)
            assertThat(projection.char).isEqualTo(entity.char)
            assertThat(projection.int).isEqualTo(entity.int)
            assertThat(projection.long).isEqualTo(entity.long)
            assertThat(projection.float).isEqualTo(entity.float)
            assertThat(projection.double).isEqualTo(entity.double)
            assertThat(projection.string).isEqualTo(entity.string)
            assertThat(projection.date?.time).isEqualTo(entity.date?.time)
            assertThat(projection.localDate).isEqualTo(entity.localDate)
            assertThat(projection.bigInteger).isEqualTo(entity.bigInteger)
            assertThat(projection.bigDecimal?.stripTrailingZeros())
                .isEqualTo(entity.bigDecimal?.stripTrailingZeros())
            assertThat(projection.blob).isEqualTo(entity.blob)
            assertThat(projection.clob).isEqualTo(entity.clob)
            assertThat(projection.enum).isEqualTo(entity.enum)
            assertThat(projection.uuid).isEqualTo(entity.uuid)
            assertThat(projection.duration).isEqualTo(entity.duration)
            assertThat(projection.instant).isEqualTo(entity.instant)
        }
    }

    @Test
    fun `should successfully project with inner projection`() {
        createDefaultTestEntities()

        projectAndVerifyEach(
            EntityWithSimpleValues::class,
            ProjectionAsInnerInterface::class,
            EntityWithSimpleValues::id,
            ProjectionAsInnerInterface::id,
            2
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.int).isEqualTo(entity.int)
            assertThat(projection.long).isEqualTo(entity.long)
        }
    }

    @Test
    fun `should successfully project with inner projection at depth 2`() {
        createDefaultTestEntities()

        projectAndVerifyEach(
            EntityWithSimpleValues::class,
            ProjectionAsInnerOfInnerInterface::class,
            EntityWithSimpleValues::id,
            ProjectionAsInnerOfInnerInterface::id,
            2
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.int).isEqualTo(entity.int)
            assertThat(projection.long).isEqualTo(entity.long)
        }
    }

    @Test
    fun `should map properties with different nullability successfully`() {
        createDefaultTestEntities()

        projectAndVerifyEach(
            EntityWithSimpleValues::class,
            ProjectionWithDifferentNullability::class,
            EntityWithSimpleValues::id,
            ProjectionWithDifferentNullability::id,
            2
        ) { entity, projection ->
            assertThat(projection.id).isEqualTo(entity.id)
            // Compare all properties with the entity
            assertThat(projection.int).isEqualTo(entity.int)
            assertThat(projection.long).isEqualTo(entity.long)
        }
    }

    @Test
    fun `should fail mapping property nullable to non-nullable when value is null`() {
        repeat(2) {
            entityManager.persist(
                EntityWithSimpleValues().apply {
                    long = null
                }
            )
        }
        entityManager.flush()

        projectAndExpectException(
            EntityWithSimpleValues::class,
            ProjectionWithDifferentNullability::class,
            IllegalArgumentException::class
        )
    }
}
