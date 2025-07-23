package com.runninglane.jpa.projection.integration.cases.elementcollection.map

import com.runninglane.jpa.projection.integration.BaseTest
import com.runninglane.jpa.projection.integration.EntityManagerWithCounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [MapElementCollectionTestConfig::class])
class MapElementCollectionTest : BaseTest() {

    @Test
    fun `should project @ElementCollection of Map(String, Int) correctly`() {
        val entity1 = EntityWithMapOfStringToInt().apply {
            elements = mutableMapOf("1" to 101, "2" to 202, "3" to 303)
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithMapOfStringToInt().apply {
            elements = mutableMapOf("4" to 404, "5" to 505, "6" to 606)
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithMapOfStringToInt::class,
            EntityWithMapOfStringToIntProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                EntityWithMapOfStringToInt::id,
                EntityWithMapOfStringToIntProjection::id,
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.elements).isEqualTo(entity.elements)
            }

            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }

            // Assert correct property values in projections
            assertThat(projection1.elements)
                .isEqualTo(entity1.elements)
                .isEqualTo(mapOf("1" to 101, "2" to 202, "3" to 303))
            assertThat(projection2.elements)
                .isEqualTo(entity2.elements)
                .isEqualTo(mapOf("4" to 404, "5" to 505, "6" to 606))
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should project @ElementCollection of Map({@Embeddable}, Double) correctly`() {
        val entity1 = EntityWithMapOfEmbeddableValueToDouble().apply {
            elements = mapOf(
                EmbeddableValue().apply {
                    short = 101.toShort()
                    double = 202.0
                    string = "Test1"
                } to 3003.0,
                EmbeddableValue().apply {
                    short = 404.toShort()
                    double = 505.0
                    string = "Test2"
                } to 6006.0
            )
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithMapOfEmbeddableValueToDouble().apply {
            elements = mapOf()
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithMapOfEmbeddableValueToDouble::class,
            EntityWithMapOfEmbeddableValueToDoubleProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                EntityWithMapOfEmbeddableValueToDouble::id,
                EntityWithMapOfEmbeddableValueToDoubleProjection::id,
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.elements).isEqualTo(entity.elements)
            }

            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }

            // Assert correct property values in projections
            assertThat(projection1.elements).hasSize(2)
            assertThat(projection1.elements)
                .isEqualTo(entity1.elements)
                .isEqualTo(
                    mapOf(
                        EmbeddableValue().apply {
                            short = 101.toShort()
                            double = 202.0
                            string = "Test1"
                        } to 3003.0,
                        EmbeddableValue().apply {
                            short = 404.toShort()
                            double = 505.0
                            string = "Test2"
                        } to 6006.0
                    )
                )
            assertThat(projection2.elements).hasSize(0)
            assertThat(projection2.elements)
                .isEqualTo(entity2.elements)
                .isEqualTo(mapOf<EmbeddableValue, Double>())
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should project @ElementCollection of Map(String, {@Embeddable}) correctly`() {
        val entity1 = EntityWithMapOfStringToEmbeddableValue().apply {
            elements = mapOf(
                "Test1" to EmbeddableValue().apply {
                    short = 101.toShort()
                    double = 202.0
                    string = "Test2"
                },
                "Test3" to EmbeddableValue().apply {
                    short = 303.toShort()
                    double = 404.0
                    string = "Test4"
                }
            )
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithMapOfStringToEmbeddableValue().apply {
            elements = mapOf()
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithMapOfStringToEmbeddableValue::class,
            EntityWithMapOfStringToEmbeddableValueProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                EntityWithMapOfStringToEmbeddableValue::id,
                EntityWithMapOfStringToEmbeddableValueProjection::id,
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.elements).isEqualTo(entity.elements)
            }

            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }

            // Assert correct property values in projections
            assertThat(projection1.elements).hasSize(2)
            assertThat(projection1.elements)
                .isEqualTo(entity1.elements)
                .isEqualTo(
                    mapOf(
                        "Test1" to EmbeddableValue().apply {
                            short = 101.toShort()
                            double = 202.0
                            string = "Test2"
                        },
                        "Test3" to EmbeddableValue().apply {
                            short = 303.toShort()
                            double = 404.0
                            string = "Test4"
                        }
                    )
                )
            assertThat(projection2.elements).hasSize(0)
            assertThat(projection2.elements).isEqualTo(entity2.elements).isEqualTo(mapOf<String, EmbeddableValue>())
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should project @ElementCollection of Map({@Embeddable}, {@Embeddable}) correctly`() {
        val entity1 = EntityWithMapOfEmbeddableValueToEmbeddableValue().apply {
            elements = mapOf(
                EmbeddableValue().apply {
                    short = 101.toShort()
                    double = 202.12
                    string = "Test1"
                } to EmbeddableValue().apply {
                    short = 303.toShort()
                    double = 404.34
                    string = "Test2"
                },
                EmbeddableValue().apply {
                    short = 505.toShort()
                    double = 606.56
                    string = "Test3"
                } to EmbeddableValue().apply {
                    short = 707.toShort()
                    double = 808.78
                    string = "Test4"
                }
            )
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithMapOfEmbeddableValueToEmbeddableValue().apply {
            elements = mapOf()
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithMapOfEmbeddableValueToEmbeddableValue::class,
            EntityWithMapOfEmbeddableValueToEmbeddableValueProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                EntityWithMapOfEmbeddableValueToEmbeddableValue::id,
                EntityWithMapOfEmbeddableValueToEmbeddableValueProjection::id,
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.elements).isEqualTo(entity.elements)
            }

            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }

            // Assert correct property values in projections
            assertThat(projection1.elements).hasSize(2)
            assertThat(projection1.elements)
                .isEqualTo(entity1.elements)
                .isEqualTo(
                    mapOf(
                        EmbeddableValue().apply {
                            short = 101.toShort()
                            double = 202.12
                            string = "Test1"
                        } to EmbeddableValue().apply {
                            short = 303.toShort()
                            double = 404.34
                            string = "Test2"
                        },
                        EmbeddableValue().apply {
                            short = 505.toShort()
                            double = 606.56
                            string = "Test3"
                        } to EmbeddableValue().apply {
                            short = 707.toShort()
                            double = 808.78
                            string = "Test4"
                        }
                    )
                )
            assertThat(projection2.elements).hasSize(0)
            assertThat(projection2.elements).isEqualTo(entity2.elements).isEqualTo(mapOf<EmbeddableValue, EmbeddableValue>())
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should project @ElementCollection of Map({simple entity}, String) correctly`() {
        val simpleEntity1 = SimpleEntity().apply {
            string = "Test1"
        }.also { entityManager.persist(it) }

        val simpleEntity2 = SimpleEntity().apply {
            string = "Test2"
        }.also { entityManager.persist(it) }

        val entity1 = EntityWithMapOfSimpleEntityToString().apply {
            elements = mapOf(
                simpleEntity1 to "Test3",
                simpleEntity2 to "Test4"
            )
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithMapOfSimpleEntityToString().apply {
            elements = mapOf()
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithMapOfSimpleEntityToString::class,
            EntityWithMapOfSimpleEntityToStringProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                EntityWithMapOfSimpleEntityToString::id,
                EntityWithMapOfSimpleEntityToStringProjection::id,
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.elements).isEqualTo(entity.elements)
            }

            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }

            // Assert correct property values in projections
            assertThat(projection1.elements).hasSize(2)
            assertThat(projection1.elements)
                .isEqualTo(entity1.elements)
                .isEqualTo(
                    mapOf(
                        simpleEntity1 to "Test3",
                        simpleEntity2 to "Test4"
                    )
                )
            assertThat(projection2.elements).hasSize(0)
            assertThat(projection2.elements).isEqualTo(entity2.elements).isEqualTo(mapOf<SimpleEntity, String>())
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should project @ElementCollection of Map({complex entity}, String) correctly`() {
        val simpleEntity1 = SimpleEntity().apply {
            string = "Test1"
        }.also { entityManager.persist(it) }

        val simpleEntity2 = SimpleEntity().apply {
            string = "Test2"
        }.also { entityManager.persist(it) }

        val complexEntity1 = ComplexEntity().apply {
            string = "Test3"
            associated = simpleEntity1
        }.also { entityManager.persist(it) }

        val complexEntity2 = ComplexEntity().apply {
            string = "Test4"
            associated = simpleEntity2
        }.also { entityManager.persist(it) }

        val entity1 = EntityWithMapOfComplexEntityToString().apply {
            elements = mapOf(
                complexEntity1 to "Test5",
                complexEntity2 to "Test6"
            )
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithMapOfComplexEntityToString().apply {
            elements = mapOf()
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithMapOfComplexEntityToString::class,
            EntityWithMapOfComplexEntityToStringProjection::class,
            2,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(
                entities,
                projections,
                EntityWithMapOfComplexEntityToString::id,
                EntityWithMapOfComplexEntityToStringProjection::id,
            ) { entity, projection ->
                // Compare all properties with the entity
                assertThat(projection.elements).isEqualTo(entity.elements)
            }

            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }

            // Assert correct property values in projections
            assertThat(projection1.elements).hasSize(2)
            assertThat(projection1.elements)
                .isEqualTo(entity1.elements)
                .isEqualTo(
                    mapOf(
                        complexEntity1 to "Test5",
                        complexEntity2 to "Test6"
                    )
                )
            assertThat(projection2.elements).hasSize(0)
            assertThat(projection2.elements).isEqualTo(entity2.elements).isEqualTo(mapOf<ComplexEntity, String>())
        }

        entityManagerWithCounter.assertQueryCount(1)
    }

    @Test
    fun `should project @ElementCollection of Map({projected simple entity}, String) correctly`() {
        val simpleEntity1 = SimpleEntity().apply {
            string = "Test1"
        }.also { entityManager.persist(it) }

        val simpleEntity2 = SimpleEntity().apply {
            string = "Test2"
        }.also { entityManager.persist(it) }

        val entity1 = EntityWithMapOfSimpleEntityToString().apply {
            elements = mapOf(
                simpleEntity1 to "Test3",
                simpleEntity2 to "Test4"
            )
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithMapOfSimpleEntityToString().apply {
            elements = mapOf()
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithMapOfSimpleEntityToString::class,
            EntityWithMapOfSimpleEntityToStringProjectionWithProjectedKey::class,
            2,
            entityManagerWithCounter
        ) { _, projections ->
            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }

            val simpleEntityProjection1 = projection1.elements?.keys?.first { it.id == simpleEntity1.id }
            val simpleEntityProjection2 = projection1.elements?.keys?.first { it.id == simpleEntity2.id }

            // Assert correct property values in projections
            simpleEntityProjection1?.string = "Test1"
            simpleEntityProjection2?.string = "Test2"
            assertThat(projection1.elements).hasSize(2)
            assertThat(projection1.elements?.get(simpleEntityProjection1)).isEqualTo("Test3")
            assertThat(projection1.elements?.get(simpleEntityProjection2)).isEqualTo("Test4")
            assertThat(projection2.elements).hasSize(0)
        }

        entityManagerWithCounter.assertQueryCount(2)
    }

    @Test
    fun `should project @ElementCollection of Map({projected complex entity}, String) correctly`() {
        val simpleEntity1 = SimpleEntity().apply {
            string = "Test1"
        }.also { entityManager.persist(it) }

        val simpleEntity2 = SimpleEntity().apply {
            string = "Test2"
        }.also { entityManager.persist(it) }

        val complexEntity1 = ComplexEntity().apply {
            string = "Test3"
            associated = simpleEntity1
        }.also { entityManager.persist(it) }

        val complexEntity2 = ComplexEntity().apply {
            string = "Test4"
            associated = simpleEntity2
        }.also { entityManager.persist(it) }

        val entity1 = EntityWithMapOfComplexEntityToString().apply {
            elements = mapOf(
                complexEntity1 to "Test5",
                complexEntity2 to "Test6"
            )
        }.also { entityManager.persist(it) }

        val entity2 = EntityWithMapOfComplexEntityToString().apply {
            elements = mapOf()
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val entityManagerWithCounter = EntityManagerWithCounter(entityManager)

        projectAndVerify(
            EntityWithMapOfComplexEntityToString::class,
            EntityWithMapOfComplexEntityToStringProjectionWithProjectedKey::class,
            2,
            entityManagerWithCounter
        ) { _, projections ->
            val projection1 = projections.first { it.id == entity1.id }
            val projection2 = projections.first { it.id == entity2.id }

            val complexEntityProjection1 = projection1.elements?.keys?.first { it.id == complexEntity1.id }
            val complexEntityProjection2 = projection1.elements?.keys?.first { it.id == complexEntity2.id }

            val simpleEntityProjection1 = complexEntityProjection1?.associated
            val simpleEntityProjection2 = complexEntityProjection2?.associated

            // Assert correct property values in projections
            assertThat(projection1.elements).hasSize(2)
            assertThat(projection1.elements).isEqualTo(
                mapOf(
                    complexEntityProjection1 to "Test5",
                    complexEntityProjection2 to "Test6"
                )
            )
            assertThat(projection2.elements).hasSize(0)

            assertThat(simpleEntityProjection1?.string).isEqualTo("Test1")
            assertThat(simpleEntityProjection2?.string).isEqualTo("Test2")

            assertThat(complexEntity1.string).isEqualTo("Test3")
            assertThat(complexEntity2.string).isEqualTo("Test4")
        }

        entityManagerWithCounter.assertQueryCount(2)
    }
}