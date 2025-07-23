package com.runninglane.jpa.projection.integration.cases.entityasvalue

import com.runninglane.facade.FacadeFactory
import com.runninglane.jpa.projection.facade.toEntity
import com.runninglane.jpa.projection.queryWithProjection
import com.runninglane.jpa.projection.integration.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import kotlin.reflect.full.isSubclassOf

@DataJpaTest
@ContextConfiguration(classes = [EntityAsValueTestConfig::class])
class EntityAsValueTest : BaseTest() {

    @Test
    fun `should correctly create projections that hold property of real entity types`() {
        val entityD1 = EntityD().apply { name = "D1" }.also { entityManager.persist(it) }
        val entityD2 = EntityD().apply { name = "D2" }.also { entityManager.persist(it) }
        val entityE1 = EntityE().apply { name = "E1" }.also { entityManager.persist(it) }
        val entityE2 = EntityE().apply { name = "E2" }.also { entityManager.persist(it) }
        val entityC = EntityC().apply { name = "C" }.also { entityManager.persist(it) }
        val entityB = EntityB().apply {
            c = entityC
            dList = mutableListOf(entityD1, entityD2)
            eList = mutableListOf(entityE1, entityE2)
            dCollection = listOf(entityD1, entityD2)
            eToString = mapOf(entityE1 to "E1", entityE2 to "E2")
        }.also { entityManager.persist(it) }
        val entityA = EntityA().apply { b = entityB }.also { entityManager.persist(it) }
        entityManager.flush()
        entityManager.clear()

        val projection = entityManager.queryWithProjection<EntityA, ProjectionA>(
            predicateBuilder = { cb, _, root ->
                cb.equal(root.get<String>("id"), entityA.id)
            }
        ).single()

        assertThat(projection.b).isNotNull()
        assertThat(projection.b!!::class).isNotEqualTo(ProjectionB::class)
        assertThat(projection.b!!::class.isSubclassOf(ProjectionB::class)).isTrue()
        assertThat(projection.b!!.c!!::class).isEqualTo(EntityC::class)
        
        assertThat(projection.b!!.dList).isNotNull()
        assertThat(projection.b!!.dList!!.size).isEqualTo(2)
        projection.b!!.dList!!.forEach {
            assertThat(it::class).isEqualTo(EntityD::class)
        }
        
        assertThat(projection.b!!.eList).isNotNull()
        assertThat(projection.b!!.eList!!.size).isEqualTo(2)
        projection.b!!.eList!!.forEach {
            assertThat(it::class).isEqualTo(EntityE::class)
        }

        assertThat(projection.b!!.dCollection).isNotNull()
        assertThat(projection.b!!.dCollection!!.size).isEqualTo(2)
        projection.b!!.dCollection!!.forEach {
            assertThat(it::class).isEqualTo(EntityD::class)
        }

        assertThat(projection.b!!.eToString).isNotNull()
        assertThat(projection.b!!.eToString!!.size).isEqualTo(2)
        projection.b!!.eToString!!.forEach { (key, _) ->
            assertThat(key::class).isEqualTo(EntityE::class)
        }

        withContext("Assert facade creation") {
            val facade = FacadeFactory.default.toEntity<EntityA, ProjectionA>(projection)

            assertThat(facade.b).isNotNull()
            assertThat(facade.b!!::class).isNotEqualTo(EntityB::class)
            assertThat(facade.b!!::class.isSubclassOf(EntityB::class)).isTrue()
            assertThat(facade.b!!.c!!::class).isEqualTo(EntityC::class)
            
            assertThat(facade.b!!.dList).isNotNull()
            assertThat(facade.b!!.dList!!.size).isEqualTo(2)
            facade.b!!.dList!!.forEach { d ->
                assertThat(d::class).isEqualTo(EntityD::class)
            }
            
            assertThat(facade.b!!.eList).isNotNull()
            assertThat(facade.b!!.eList!!.size).isEqualTo(2)
            facade.b!!.eList!!.forEach { e ->
                assertThat(e::class).isEqualTo(EntityE::class)
            }

            assertThat(facade.b!!.dCollection).isNotNull()
            assertThat(facade.b!!.dCollection!!.size).isEqualTo(2)
            facade.b!!.dCollection!!.forEach { d ->
                assertThat(d::class).isEqualTo(EntityD::class)
            }

            assertThat(facade.b!!.eToString).isNotNull()
            assertThat(facade.b!!.eToString!!.size).isEqualTo(2)
            facade.b!!.eToString!!.forEach { (e, _) ->
                assertThat(e::class).isEqualTo(EntityE::class)
            }

            assertThat(facade.b!!.c).isSameAs(facade.b!!.c)

            facade.b!!.dList!!.forEach { d ->
                val fromFacade = facade.b!!.dList!!.first { it.id == d.id}
                assertThat(d).isSameAs(fromFacade)
            }

            facade.b!!.eList!!.forEach { e ->
                val fromFacade = facade.b!!.eList!!.first { it.id == e.id}
                assertThat(e).isSameAs(fromFacade)
            }

            facade.b!!.dCollection!!.forEach { d ->
                val fromFacade = facade.b!!.dCollection!!.first { it.id == d.id}
                assertThat(d).isSameAs(fromFacade)
            }

            facade.b!!.eToString!!.forEach { (e, _) ->
                val fromFacade = facade.b!!.eToString!!.entries.first { it.key.id == e.id }.key
                assertThat(e).isSameAs(fromFacade)
            }
        }
    }

}
