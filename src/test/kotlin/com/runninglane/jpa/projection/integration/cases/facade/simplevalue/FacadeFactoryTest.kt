package com.runninglane.jpa.projection.integration.cases.facade.simplevalue

import com.runninglane.facade.FacadeFactory
import com.runninglane.jpa.projection.facade.toEntity
import com.runninglane.jpa.projection.queryWithProjection
import com.runninglane.jpa.projection.integration.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.util.*

@DataJpaTest
@ContextConfiguration(classes = [FacadeFactoryTestConfig::class])
class FacadeFactoryTest : BaseTest() {
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

    fun insertSimpleValues(): EntityWithSimpleValues {
        return EntityWithSimpleValues().apply {
            boolean = true
            byte = 1
            short = 2
            char = 'A'
            int = 100
            long = 200L
            float = 1.5f
            double = 2.5
            string = "Test1"
            date = Date()
            localDate = LocalDate.now()
            bigInteger = BigInteger.valueOf(300)
            bigDecimal = BigDecimal("400.5")
            blob = byteArrayOf(1, 2, 3)
            clob = "Test CLOB 1"
            enum = TestEnum.ONE
            uuid = UUID.randomUUID()
            duration = Duration.ofHours(1)
            instant = Instant.now()
        }.also { entityManager.persist(it) }
    }

    @Test
    fun `should create correct entity facade from projection with exact types`() {
        val entity = insertSimpleValues()

        val projection = entityManager.queryWithProjection<EntityWithSimpleValues, ProjectionWithExactTypes>().single()
        val entityFacade: EntityWithSimpleValues = FacadeFactory.default
            .toEntity<EntityWithSimpleValues, ProjectionWithExactTypes>(projection)

        assertThat(entityFacade.id).isEqualTo(entity.id)
        assertThat(entityFacade.boolean).isEqualTo(entity.boolean)
        assertThat(entityFacade.byte).isEqualTo(entity.byte)
        assertThat(entityFacade.short).isEqualTo(entity.short)
        assertThat(entityFacade.char).isEqualTo(entity.char)
        assertThat(entityFacade.int).isEqualTo(entity.int)
        assertThat(entityFacade.long).isEqualTo(entity.long)
        assertThat(entityFacade.float).isEqualTo(entity.float)
        assertThat(entityFacade.double).isEqualTo(entity.double)
        assertThat(entityFacade.string).isEqualTo(entity.string)
        assertThat(entityFacade.date?.time).isEqualTo(entity.date?.time)
        assertThat(entityFacade.localDate).isEqualTo(entity.localDate)
        assertThat(entityFacade.bigInteger).isEqualTo(entity.bigInteger)
        assertThat(entityFacade.bigDecimal?.stripTrailingZeros()).isEqualTo(entity.bigDecimal?.stripTrailingZeros())
        assertThat(entityFacade.blob).isEqualTo(entity.blob)
        assertThat(entityFacade.clob).isEqualTo(entity.clob)
        assertThat(entityFacade.enum).isEqualTo(entity.enum)
        assertThat(entityFacade.uuid).isEqualTo(entity.uuid)
        assertThat(entityFacade.duration).isEqualTo(entity.duration)
        assertThat(entityFacade.instant).isEqualTo(entity.instant)
    }

    @Test
    fun `should create correct entity facade from projection with different nullability and fail when accessing unprojected properties`() {
        val entity = insertSimpleValues()

        val projection = entityManager.queryWithProjection<EntityWithSimpleValues, ProjectionWithDifferentNullability>().single()
        val entityFacade: EntityWithSimpleValues = FacadeFactory.default
            .toEntity<EntityWithSimpleValues, ProjectionWithDifferentNullability>(projection)

        assertThat(entityFacade.id).isEqualTo(entity.id)
        assertThat(entityFacade.int).isEqualTo(entity.int)
        assertThat(entityFacade.long).isEqualTo(entity.long)
        assertThrows<UnsupportedOperationException> { entityFacade.boolean }
        assertThrows<UnsupportedOperationException> { entityFacade.byte }
        assertThrows<UnsupportedOperationException> { entityFacade.short }
        assertThrows<UnsupportedOperationException> { entityFacade.char }
        assertThrows<UnsupportedOperationException> { entityFacade.float }
        assertThrows<UnsupportedOperationException> { entityFacade.double }
        assertThrows<UnsupportedOperationException> { entityFacade.string }
        assertThrows<UnsupportedOperationException> { entityFacade.date }
        assertThrows<UnsupportedOperationException> { entityFacade.localDate }
        assertThrows<UnsupportedOperationException> { entityFacade.bigInteger }
        assertThrows<UnsupportedOperationException> { entityFacade.bigDecimal }
        assertThrows<UnsupportedOperationException> { entityFacade.blob }
        assertThrows<UnsupportedOperationException> { entityFacade.clob }
        assertThrows<UnsupportedOperationException> { entityFacade.enum }
        assertThrows<UnsupportedOperationException> { entityFacade.uuid }
        assertThrows<UnsupportedOperationException> { entityFacade.duration }
        assertThrows<UnsupportedOperationException> { entityFacade.instant }
    }

    @Test
    fun `should create correct entity facade from projection with inner depth 1`() {
        val entity = insertSimpleValues()

        val projection = entityManager.queryWithProjection<EntityWithSimpleValues, ProjectionAsInnerInterface>().single()
        val entityFacade: EntityWithSimpleValues = FacadeFactory.default
            .toEntity<EntityWithSimpleValues, ProjectionAsInnerInterface>(projection)

        assertThat(entityFacade.id).isEqualTo(entity.id)
        assertThat(entityFacade.int).isEqualTo(entity.int)
        assertThat(entityFacade.long).isEqualTo(entity.long)
    }

    @Test
    fun `should create correct entity facade from projection with inner depth 2`() {
        val entity = insertSimpleValues()

        val projection = entityManager.queryWithProjection<EntityWithSimpleValues, InnerDepth1.ProjectionAsInnerOfInnerInterface>().single()
        val entityFacade: EntityWithSimpleValues = FacadeFactory.default
            .toEntity<EntityWithSimpleValues, InnerDepth1.ProjectionAsInnerOfInnerInterface>(projection)

        assertThat(entityFacade.id).isEqualTo(entity.id)
        assertThat(entityFacade.int).isEqualTo(entity.int)
        assertThat(entityFacade.long).isEqualTo(entity.long)
    }
}
