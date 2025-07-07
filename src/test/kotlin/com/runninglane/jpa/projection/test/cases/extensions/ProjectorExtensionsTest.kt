package com.runninglane.jpa.projection.test.cases.extensions

import com.runninglane.jpa.projection.createProjectionQuery
import com.runninglane.jpa.projection.createProjector
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration


@DataJpaTest
@ContextConfiguration(classes = [ExtensionsTestConfiguration::class])
class ProjectorExtensionsTest : BaseExtensionsTest() {

    @Test
    fun `ProjectionQueryBuilder(entityClass, projectionClass, predicateBuilder, ordersBuilder, firstResult, maxResults) should return correct projections`() {
        createDefaultTestEntities()

        val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()
        val projections = entityManager.createProjectionQuery(projector)
            .where { cb, _, root ->
                cb.greaterThan(root.get("int"), 5)
            }
            .orderBy { cb, root ->
                arrayOf(cb.desc(root.get<Int>("int")))
            }
            .firstResult(2)
            .maxResults(3)
            .resultList

        assertThat(projections).hasSize(3)
        assertThat(projections[0].int).isEqualTo(8)
        assertThat(projections[1].int).isEqualTo(7)
        assertThat(projections[2].int).isEqualTo(6)
        assertThat(projections[0].string).isEqualTo("Test8")
        assertThat(projections[1].string).isEqualTo("Test7")
        assertThat(projections[2].string).isEqualTo("Test6")
    }

    @Test
    fun `ProjectionQueryBuilder{E, P}(predicateBuilder, ordersBuilder, firstResult, maxResults) should return correct projections`() {
        createDefaultTestEntities()

        val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()
        val projections = entityManager.createProjectionQuery(projector)
            .where { cb, _, root ->
                cb.greaterThan(root.get("int"), 4)
            }
            .orderBy { cb, root ->
                arrayOf(cb.asc(root.get<Int>("int")))
            }
            .firstResult(2)
            .maxResults(3)
            .resultList
        
        assertThat(projections).hasSize(3)
        assertThat(projections[0].int).isEqualTo(7)
        assertThat(projections[1].int).isEqualTo(8)
        assertThat(projections[2].int).isEqualTo(9)
        assertThat(projections[0].string).isEqualTo("Test7")
        assertThat(projections[1].string).isEqualTo("Test8")
        assertThat(projections[2].string).isEqualTo("Test9")
    }

    @Test
    fun `ProjectionQueryBuilder{E, P}(predicateBuilder, firstResult, maxResults) should return correct projections`() {
        createDefaultTestEntities()

        val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()
        val projections = entityManager.createProjectionQuery(projector)
            .where { cb, _, root ->
                cb.greaterThan(root.get("int"), 1)
            }
            .firstResult(2)
            .maxResults(4)
            .resultList
        
        assertThat(projections).hasSize(4)
        assertThat(projections[0].int).isEqualTo(4)
        assertThat(projections[1].int).isEqualTo(5)
        assertThat(projections[2].int).isEqualTo(6)
        assertThat(projections[3].int).isEqualTo(7)
        assertThat(projections[0].string).isEqualTo("Test4")
        assertThat(projections[1].string).isEqualTo("Test5")
        assertThat(projections[2].string).isEqualTo("Test6")
        assertThat(projections[3].string).isEqualTo("Test7")
    }

    @Test
    fun `ProjectionQueryBuilder{E, P}(ordersBuilder, firstResult, maxResults) should return correct projections`() {
        createDefaultTestEntities()

        val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()
        val projections = entityManager.createProjectionQuery(projector)
            .orderBy { cb, root ->
                arrayOf(cb.desc(root.get<Int>("int")))
            }
            .firstResult(2)
            .maxResults(3)
            .resultList
        
        assertThat(projections).hasSize(3)
        assertThat(projections[0].int).isEqualTo(8)
        assertThat(projections[1].int).isEqualTo(7)
        assertThat(projections[2].int).isEqualTo(6)
        assertThat(projections[0].string).isEqualTo("Test8")
        assertThat(projections[1].string).isEqualTo("Test7")
        assertThat(projections[2].string).isEqualTo("Test6")
    }

    @Test
    fun `ProjectionQueryBuilder{E, P}(firstResult, maxResults) should return correct projections`() {
        createDefaultTestEntities()

        val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()
        val projections = entityManager.createProjectionQuery(projector)
            .firstResult(2)
            .maxResults(3)
            .resultList
        
        assertThat(projections).hasSize(3)
        assertThat(projections[0].int).isEqualTo(3)
        assertThat(projections[1].int).isEqualTo(4)
        assertThat(projections[2].int).isEqualTo(5)
        assertThat(projections[0].string).isEqualTo("Test3")
        assertThat(projections[1].string).isEqualTo("Test4")
        assertThat(projections[2].string).isEqualTo("Test5")
    }

    @Test
    fun `ProjectionQueryBuilder{E, P}(firstResult) should return correct projections`() {
        createDefaultTestEntities()

        val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()
        val projections = entityManager.createProjectionQuery(projector)
            .firstResult(2)
            .resultList
        
        assertThat(projections).hasSize(8)
        assertThat(projections[0].int).isEqualTo(3)
        assertThat(projections[1].int).isEqualTo(4)
        assertThat(projections[2].int).isEqualTo(5)
        assertThat(projections[3].int).isEqualTo(6)
        assertThat(projections[4].int).isEqualTo(7)
        assertThat(projections[5].int).isEqualTo(8)
        assertThat(projections[6].int).isEqualTo(9)
        assertThat(projections[7].int).isEqualTo(10)
        assertThat(projections[0].string).isEqualTo("Test3")
        assertThat(projections[1].string).isEqualTo("Test4")
        assertThat(projections[2].string).isEqualTo("Test5")
        assertThat(projections[3].string).isEqualTo("Test6")
        assertThat(projections[4].string).isEqualTo("Test7")
        assertThat(projections[5].string).isEqualTo("Test8")
        assertThat(projections[6].string).isEqualTo("Test9")
        assertThat(projections[7].string).isEqualTo("Test10")
    }

    @Test
    fun `ProjectionQueryBuilder{E, P}(maxResults) should return correct projections`() {
        createDefaultTestEntities()

        val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()
        val projections = entityManager.createProjectionQuery(projector)
            .maxResults(3)
            .resultList
        
        assertThat(projections).hasSize(3)
        assertThat(projections[0].int).isEqualTo(1)
        assertThat(projections[1].int).isEqualTo(2)
        assertThat(projections[2].int).isEqualTo(3)
        assertThat(projections[0].string).isEqualTo("Test1")
        assertThat(projections[1].string).isEqualTo("Test2")
        assertThat(projections[2].string).isEqualTo("Test3")
    }

    @Test
    fun `ProjectionQueryBuilder{E, P}(predicateBuilder, firstResult) should throw IllegalArgumentException when firstResult is negative`() {
        createDefaultTestEntities()

        assertThrows<IllegalArgumentException> {
            val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()
            entityManager.createProjectionQuery(projector)
                .where { cb, _, root ->
                    cb.greaterThan(root.get("int"), 8)
                }
                .firstResult(-1)
                .resultList
        }
    }

    @Test
    fun `ProjectionQueryBuilder{E, P}(predicateBuilder, firstResult) should return correct projections when firstResult is more than total entities`() {
        createDefaultTestEntities()

        val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()
        val projections = entityManager.createProjectionQuery(projector)
            .where { cb, _, root ->
                cb.greaterThan(root.get("int"), 8)
            }
            .firstResult(3)
            .resultList
        
        assertThat(projections).hasSize(0)
    }

    @Test
    fun `ProjectionQueryBuilder{E, P}(predicateBuilder, maxResults) should return correct projections when maxResults is less than total entities`() {
        createDefaultTestEntities()

        val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()
        val projections = entityManager.createProjectionQuery(projector)
            .where { cb, _, root ->
                cb.greaterThan(root.get("int"), 8)
            }
            .maxResults(1)
            .resultList
        
        assertThat(projections).hasSize(1)
        assertThat(projections[0].int).isEqualTo(9)
        assertThat(projections[0].string).isEqualTo("Test9")
    }

    @Test
    fun `ProjectionQueryBuilder{E, P}(predicateBuilder, maxResults) should return correct projections when maxResults is more than total entities`() {
        createDefaultTestEntities()

        val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()
        val projections = entityManager.createProjectionQuery(projector)
            .where { cb, _, root ->
                cb.greaterThan(root.get("int"), 8)
            }
            .maxResults(3)
            .resultList
        
        assertThat(projections).hasSize(2)
        assertThat(projections[0].int).isEqualTo(9)
        assertThat(projections[1].int).isEqualTo(10)
        assertThat(projections[0].string).isEqualTo("Test9")
        assertThat(projections[1].string).isEqualTo("Test10")
    }

    @Test
    fun `ProjectionQueryBuilder{E, P}(predicateBuilder) should return correct projections when subquery is used`() {
        createDefaultTestEntities()

        val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()
        val projections = entityManager.createProjectionQuery(projector)
            .where { cb, query, root ->
                // Exists another VerySimpleEntity that having int value 1/3 of its int value
                cb.exists(query.subquery(VerySimpleEntity::class.java).also { sq ->
                    val anotherRoot = sq.from(VerySimpleEntity::class.java)
                    sq.select(anotherRoot)
                    sq.where(
                        cb.equal(
                            cb.prod(anotherRoot.get("int"), 3),
                            root.get<Int>("int")
                        )
                    )
                })
            }
            .resultList
        
        assertThat(projections).hasSize(3)
        assertThat(projections[0].int).isEqualTo(3)
        assertThat(projections[1].int).isEqualTo(6)
        assertThat(projections[2].int).isEqualTo(9)
        assertThat(projections[0].string).isEqualTo("Test3")
        assertThat(projections[1].string).isEqualTo("Test6")
        assertThat(projections[2].string).isEqualTo("Test9")
    }

    @Test
    fun `ProjectionQueryBuilder's whereMatchExample() should return correct projections`() {
        createDefaultTestEntities()

        val projector = entityManager.createProjector<VerySimpleEntity, VerySimpleEntityProjection>()

        val the8 = entityManager.createProjectionQuery(projector)
            .where { cb, _, root ->
                cb.equal(root.get<Int>("int"), 8)
            }
            .resultList
            .single()

        val the8Second = entityManager.createProjectionQuery(projector)
            .whereMatchExample("id" to the8.id)
            .resultList
            .single()

        assertThat(the8Second.id).isEqualTo(the8.id)
        assertThat(the8Second.int).isEqualTo(the8.int).isEqualTo(8)
        assertThat(the8Second.string).isEqualTo(the8.string).isEqualTo("Test8")

        val the8Third = entityManager.createProjectionQuery(projector)
            .whereMatchExample("id" to the8.id, "string" to "Test8")
            .resultList
            .single()

        assertThat(the8Third.id).isEqualTo(the8.id)
        assertThat(the8Third.int).isEqualTo(the8.int).isEqualTo(8)
        assertThat(the8Third.string).isEqualTo(the8.string).isEqualTo("Test8")

        val the8Fourth = entityManager.createProjectionQuery(projector)
            .whereMatchExample("id" to the8.id, "string" to "Test10")
            .resultList
            .singleOrNull()

        assertThat(the8Fourth).isNull()

        val the12 = entityManager.createProjectionQuery(projector)
            .whereMatchExample("string" to "Test12")
            .resultList
            .singleOrNull()

        assertThat(the12).isNull()
    }

    @Test
    fun `EntityManager's createProjectionQuery() with no argument should return correct projections`() {
        createDefaultTestEntities()

        val the8 = entityManager.createProjectionQuery<VerySimpleEntity, VerySimpleEntityProjection>()
            .where { cb, _, root ->
                cb.equal(root.get<Int>("int"), 8)
            }
            .resultList
            .single()

        assertThat(the8.int).isEqualTo(8)
        assertThat(the8.string).isEqualTo("Test8")
    }
}
