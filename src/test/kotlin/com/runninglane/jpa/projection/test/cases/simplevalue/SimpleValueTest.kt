package com.runninglane.jpa.projection.test.cases.simplevalue

import com.runninglane.jpa.projection.queryWithProjection
import com.runninglane.jpa.projection.test.BaseTest
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

        entityManager.queryWithProjection<EntityWithSimpleValues, ProjectionWithExactTypes>()

        TODO("Not yet implemented")
    }
}
