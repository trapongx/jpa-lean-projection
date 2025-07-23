package com.runninglane.jpa.projection.integration.cases.elementcollection.collection

import com.runninglane.jpa.projection.integration.BaseTest
import com.runninglane.jpa.projection.integration.EntityManagerWithCounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


@DataJpaTest
@ContextConfiguration(classes = [ElementCollectionTestConfig::class])
class ElementCollectionTest : BaseTest() {

    private fun LocalDateTime.toInstantUTC() = this.toInstant(ZoneOffset.UTC)

    private fun Collection<LocalDateTime>.toInstantSetUTC(): Set<Instant> = this.map { it.toInstantUTC() }.toSet()

    @Test
    fun `should project @ElementCollection of List of String correctly`() {
        val entity1 = EntityWithListOfString().apply {
            elements = listOf("Java", "Kotlin", "Spring")
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithListOfString().apply {
            elements = listOf("Pascal", "Delphi", "C++")
        }.also { entityManager.persist(it) }

        val entity3 = EntityWithListOfString().apply {
            elements = listOf("Go", "Cobol", "Rust")
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithListOfString::class,
            EntityWithListOfStringProjection::class,
            3,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                EntityWithListOfString::id,
                EntityWithListOfStringProjection::id,
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.elements).isEqualTo(entity.elements)
            }

            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }
            val projection3 = projections.first { it.id == entity3.id }

            // Assert correct property values in projections
            assertThat(projection1.elements).isEqualTo(entity1.elements).isEqualTo(listOf("Java", "Kotlin", "Spring"))
            assertThat(projection2.elements).isEqualTo(entity2.elements).isEqualTo(listOf("Pascal", "Delphi", "C++"))
            assertThat(projection3.elements).isEqualTo(entity3.elements).isEqualTo(listOf("Go", "Cobol", "Rust"))
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should project @ElementCollection of 2 Lists of String correctly`() {
        val entity1 = EntityWithTwoListOfString().apply {
            elements1 = listOf("Java", "Kotlin", "Spring")
            elements2 = listOf("1", "2", "3")
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithTwoListOfString().apply {
            elements1 = listOf("Pascal", "Delphi", "C++")
            elements2 = listOf("4", "5", "6")
        }.also { entityManager.persist(it) }

        val entity3 = EntityWithTwoListOfString().apply {
            elements1 = listOf("Go", "Cobol", "Rust")
            elements2 = listOf("7", "8", "9")
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithTwoListOfString::class,
            EntityWithTwoListOfStringProjection::class,
            3,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                EntityWithTwoListOfString::id,
                EntityWithTwoListOfStringProjection::id,
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.elements1).isEqualTo(entity.elements1)
                assertThat(projection.elements2).isEqualTo(entity.elements2)
            }

            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }
            val projection3 = projections.first { it.id == entity3.id }

            // Assert correct property values in projections
            assertThat(projection1.elements1).isEqualTo(entity1.elements1).isEqualTo(listOf("Java", "Kotlin", "Spring"))
            assertThat(projection1.elements2).isEqualTo(entity1.elements2).isEqualTo(listOf("1", "2", "3"))
            assertThat(projection2.elements1).isEqualTo(entity2.elements1).isEqualTo(listOf("Pascal", "Delphi", "C++"))
            assertThat(projection2.elements2).isEqualTo(entity2.elements2).isEqualTo(listOf("4", "5", "6"))
            assertThat(projection3.elements1).isEqualTo(entity3.elements1).isEqualTo(listOf("Go", "Cobol", "Rust"))
            assertThat(projection3.elements2).isEqualTo(entity3.elements2).isEqualTo(listOf("7", "8", "9"))
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should project @ElementCollection of Collection of Int correctly`() {
        val entity1 = EntityWithCollectionOfInt().apply {
            elements = listOf(1, 2, 3)
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithCollectionOfInt().apply {
            elements = listOf(4, 5, 6)
        }.also { entityManager.persist(it) }

        val entity3 = EntityWithCollectionOfInt().apply {
            elements = listOf(7, 8, 9)
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithCollectionOfInt::class,
            EntityWithCollectionOfIntProjection::class,
            3,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                EntityWithCollectionOfInt::id,
                EntityWithCollectionOfIntProjection::id,
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.elements).isEqualTo(entity.elements)
            }

            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }
            val projection3 = projections.first { it.id == entity3.id }

            // Assert correct property values in projections
            assertThat(projection1.elements).isEqualTo(entity1.elements).isEqualTo(listOf(1, 2, 3))
            assertThat(projection2.elements).isEqualTo(entity2.elements).isEqualTo(listOf(4, 5, 6))
            assertThat(projection3.elements).isEqualTo(entity3.elements).isEqualTo(listOf(7, 8, 9))
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should project @ElementCollection of Set of LocalDateTime correctly`() {
        val localDateTime1 = LocalDateTime.of(2022, 1, 2, 3, 4)
        val localDateTime2 = LocalDateTime.of(2022, 2, 3, 4, 5)
        val localDateTime3 = LocalDateTime.of(2022, 3, 4, 5, 6)
        val localDateTime4 = LocalDateTime.of(2022, 4, 5, 6, 7)

        val entity1 = EntityWithSetOfLocalDateTime().apply {
            elements = setOf(localDateTime1, localDateTime2)
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithSetOfLocalDateTime().apply {
            elements = setOf(localDateTime3, localDateTime4)
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithSetOfLocalDateTime::class,
            EntityWithSetOfLocalDateTimeProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                EntityWithSetOfLocalDateTime::id,
                EntityWithSetOfLocalDateTimeProjection::id,
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.elements?.toInstantSetUTC())
                    .isEqualTo(entity.elements?.toInstantSetUTC())
            }

            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }

            // Assert correct property values in projections
            assertThat(projection1.elements?.toInstantSetUTC())
                .isEqualTo(entity1.elements?.toInstantSetUTC())
                .isEqualTo(listOf(localDateTime1, localDateTime2).toInstantSetUTC())
            assertThat(projection2.elements?.toInstantSetUTC())
                .isEqualTo(entity2.elements?.toInstantSetUTC())
                .isEqualTo(listOf(localDateTime3, localDateTime4).toInstantSetUTC())
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should project @ElementCollection of List of String with inverted nullability and mutability correctly`() {
        val entity1 = EntityWithListOfString().apply {
            elements = listOf("Java", "Kotlin", "Spring")
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithListOfString().apply {
            elements = listOf("Pascal", "Delphi", "C++")
        }.also { entityManager.persist(it) }

        val entity3 = EntityWithListOfString().apply {
            elements = listOf("Go", "Cobol", "Rust")
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithListOfString::class,
            EntityWithListOfStringProjectionWithInvertedNullabilityAndMutability::class,
            3,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                EntityWithListOfString::id,
                EntityWithListOfStringProjectionWithInvertedNullabilityAndMutability::id,
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.elements).isEqualTo(entity.elements)
            }

            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }
            val projection3 = projections.first { it.id == entity3.id }

            // Assert correct property values in projections
            assertThat(projection1.elements)
                .isEqualTo(entity1.elements)
                .isEqualTo(listOf("Java", "Kotlin", "Spring"))
            projection1.elements.add("Scala")
            assertThat(projection1.elements).isEqualTo(listOf("Java", "Kotlin", "Spring", "Scala"))

            assertThat(projection2.elements)
                .isEqualTo(entity2.elements)
                .isEqualTo(listOf("Pascal", "Delphi", "C++"))
            projection2.elements.remove("ColdFusion")
            assertThat(projection2.elements).isEqualTo(listOf("Pascal", "Delphi", "C++"))

            assertThat(projection3.elements)
                .isEqualTo(entity3.elements)
                .isEqualTo(listOf("Go", "Cobol", "Rust"))
            projection3.elements.remove("Go")
            assertThat(projection3.elements).isEqualTo(listOf("Cobol", "Rust"))
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should project @ElementCollection of List of embeddable object correctly`() {
        val entity1 = EntityWithListOfEmbeddableValue().apply {
            elements = listOf(
                EmbeddableValue(12, 99.01, "Test1"),
                EmbeddableValue(55, 0.62, "Test2")
            )
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithListOfEmbeddableValue().apply {
            elements = listOf(
                EmbeddableValue(7, 14.0, "Test3"),
            )
        }.also { entityManager.persist(it) }

        val entity3 = EntityWithListOfEmbeddableValue().apply {
            elements = emptyList()
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithListOfEmbeddableValue::class,
            EntityWithListOfEmbeddableValueProjection::class,
            3,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                EntityWithListOfEmbeddableValue::id,
                EntityWithListOfEmbeddableValueProjection::id,
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.elements.size).isEqualTo(entity.elements.size)
                (projection.elements)
                    .zip(entity.elements) { elementProjection, element ->
                        assertThat(elementProjection.short).isEqualTo(element.short)
                        assertThat(elementProjection.double).isEqualTo(element.double)
                        assertThat(elementProjection.string).isEqualTo(element.string)
                    }
            }

            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }
            val projection3 = projections.first { it.id == entity3.id }

            // Assert correct property values in projections
            assertThat(projection1.elements).hasSize(2)
            assertThat(projection1.elements[0].short).isEqualTo(entity1.elements[0].short).isEqualTo(12)
            assertThat(projection1.elements[0].double).isEqualTo(entity1.elements[0].double).isEqualTo(99.01)
            assertThat(projection1.elements[0].string).isEqualTo(entity1.elements[0].string).isEqualTo("Test1")
            assertThat(projection1.elements[1].short).isEqualTo(entity1.elements[1].short).isEqualTo(55)
            assertThat(projection1.elements[1].double).isEqualTo(entity1.elements[1].double).isEqualTo(0.62)
            assertThat(projection1.elements[1].string).isEqualTo(entity1.elements[1].string).isEqualTo("Test2")
            assertThat(projection2.elements).hasSize(1)
            assertThat(projection2.elements[0].short).isEqualTo(entity2.elements[0].short).isEqualTo(7)
            assertThat(projection2.elements[0].double).isEqualTo(entity2.elements[0].double).isEqualTo(14.0)
            assertThat(projection2.elements[0].string).isEqualTo(entity2.elements[0].string).isEqualTo("Test3")
            assertThat(projection3.elements).hasSize(0)
        }

        entityManagerWithCounter.assertQueryCount(1)
    }
}
