package com.runninglane.jpa.projection.test.cases.extensions

import com.runninglane.jpa.projection.queryWithProjection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration


@DataJpaTest
@ContextConfiguration(classes = [ExtensionsTestConfiguration::class])
class EntityManagerExtensionsTest : BaseExtensionsTest() {

    @Test
    fun `queryWithProjection(entityClass, projectionClass, predicateBuilder, ordersBuilder, firstResult, maxResults) should return correct projections`() {
        createDefaultTestEntities()

        val projections = entityManager.queryWithProjection(
            entityClass = VerySimpleEntity::class,
            projectionClass = VerySimpleEntityProjection::class,
            predicateBuilder = { cb, _, root ->
                cb.greaterThan(root.get("int"), 5)
            },
            ordersBuilder = { cb, root ->
                arrayOf(cb.desc(root.get<Int>("int")))
            },
            firstResult = 2,
            maxResults = 3
        )

        assertThat(projections).hasSize(3)
        assertThat(projections[0].int).isEqualTo(8)
        assertThat(projections[1].int).isEqualTo(7)
        assertThat(projections[2].int).isEqualTo(6)
        assertThat(projections[0].string).isEqualTo("Test8")
        assertThat(projections[1].string).isEqualTo("Test7")
        assertThat(projections[2].string).isEqualTo("Test6")
    }

    @Test
    fun `queryWithProjection{E, P}(predicateBuilder, ordersBuilder, firstResult, maxResults) should return correct projections`() {
        createDefaultTestEntities()

        val projections = entityManager.queryWithProjection<VerySimpleEntity, VerySimpleEntityProjection>(
            predicateBuilder = { cb, _, root ->
                cb.greaterThan(root.get("int"), 4)
            },
            ordersBuilder = { cb, root ->
                arrayOf(cb.asc(root.get<Int>("int")))
            },
            firstResult = 2,
            maxResults = 3
        )

        assertThat(projections).hasSize(3)
        assertThat(projections[0].int).isEqualTo(7)
        assertThat(projections[1].int).isEqualTo(8)
        assertThat(projections[2].int).isEqualTo(9)
        assertThat(projections[0].string).isEqualTo("Test7")
        assertThat(projections[1].string).isEqualTo("Test8")
        assertThat(projections[2].string).isEqualTo("Test9")
    }

    @Test
    fun `queryWithProjection{E, P}(predicateBuilder, firstResult, maxResults) should return correct projections`() {
        createDefaultTestEntities()

        val projections = entityManager.queryWithProjection<VerySimpleEntity, VerySimpleEntityProjection>(
            predicateBuilder = { cb, _, root ->
                cb.greaterThan(root.get("int"), 1)
            },
            firstResult = 2,
            maxResults = 4
        )

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
    fun `queryWithProjection{E, P}(ordersBuilder, firstResult, maxResults) should return correct projections`() {
        createDefaultTestEntities()

        val projections = entityManager.queryWithProjection<VerySimpleEntity, VerySimpleEntityProjection>(
            ordersBuilder = { cb, root ->
                arrayOf(cb.desc(root.get<Int>("int")))
            },
            firstResult = 2,
            maxResults = 3
        )

        assertThat(projections).hasSize(3)
        assertThat(projections[0].int).isEqualTo(8)
        assertThat(projections[1].int).isEqualTo(7)
        assertThat(projections[2].int).isEqualTo(6)
        assertThat(projections[0].string).isEqualTo("Test8")
        assertThat(projections[1].string).isEqualTo("Test7")
        assertThat(projections[2].string).isEqualTo("Test6")
    }

    @Test
    fun `queryWithProjection{E, P}(firstResult, maxResults) should return correct projections`() {
        createDefaultTestEntities()

        val projections = entityManager.queryWithProjection<VerySimpleEntity, VerySimpleEntityProjection>(
            firstResult = 2,
            maxResults = 3
        )

        assertThat(projections).hasSize(3)
        assertThat(projections[0].int).isEqualTo(3)
        assertThat(projections[1].int).isEqualTo(4)
        assertThat(projections[2].int).isEqualTo(5)
        assertThat(projections[0].string).isEqualTo("Test3")
        assertThat(projections[1].string).isEqualTo("Test4")
        assertThat(projections[2].string).isEqualTo("Test5")
    }

    @Test
    fun `queryWithProjection{E, P}(firstResult) should return correct projections`() {
        createDefaultTestEntities()

        val projections = entityManager.queryWithProjection<VerySimpleEntity, VerySimpleEntityProjection>(
            firstResult = 2
        )

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
    fun `queryWithProjection{E, P}(maxResults) should return correct projections`() {
        createDefaultTestEntities()

        val projections = entityManager.queryWithProjection<VerySimpleEntity, VerySimpleEntityProjection>(
            maxResults = 3
        )

        assertThat(projections).hasSize(3)
        assertThat(projections[0].int).isEqualTo(1)
        assertThat(projections[1].int).isEqualTo(2)
        assertThat(projections[2].int).isEqualTo(3)
        assertThat(projections[0].string).isEqualTo("Test1")
        assertThat(projections[1].string).isEqualTo("Test2")
        assertThat(projections[2].string).isEqualTo("Test3")
    }

    @Test
    fun `queryWithProjection{E, P}(predicateBuilder, firstResult) should throw IllegalArgumentException when firstResult is negative`() {
        createDefaultTestEntities()

        assertThrows<IllegalArgumentException> {
            entityManager.queryWithProjection<VerySimpleEntity, VerySimpleEntityProjection>(
                predicateBuilder = { cb, _, root ->
                    cb.greaterThan(root.get("int"), 8)
                },
                firstResult = -1
            )
        }
    }

    @Test
    fun `queryWithProjection{E, P}(predicateBuilder, firstResult) should return correct projections when firstResult is more than total entities`() {
        createDefaultTestEntities()

        val projections = entityManager.queryWithProjection<VerySimpleEntity, VerySimpleEntityProjection>(
            predicateBuilder = { cb, _, root ->
                cb.greaterThan(root.get("int"), 8)
            },
            firstResult = 3
        )

        assertThat(projections).hasSize(0)
    }

    @Test
    fun `queryWithProjection{E, P}(predicateBuilder, maxResults) should return correct projections when maxResults is less than total entities`() {
        createDefaultTestEntities()

        val projections = entityManager.queryWithProjection<VerySimpleEntity, VerySimpleEntityProjection>(
            predicateBuilder = { cb, _, root ->
                cb.greaterThan(root.get("int"), 8)
            },
            maxResults = 1
        )

        assertThat(projections).hasSize(1)
        assertThat(projections[0].int).isEqualTo(9)
        assertThat(projections[0].string).isEqualTo("Test9")
    }

    @Test
    fun `queryWithProjection{E, P}(predicateBuilder, maxResults) should return correct projections when maxResults is more than total entities`() {
        createDefaultTestEntities()

        val projections = entityManager.queryWithProjection<VerySimpleEntity, VerySimpleEntityProjection>(
            predicateBuilder = { cb, _, root ->
                cb.greaterThan(root.get("int"), 8)
            },
            maxResults = 3
        )

        assertThat(projections).hasSize(2)
        assertThat(projections[0].int).isEqualTo(9)
        assertThat(projections[1].int).isEqualTo(10)
        assertThat(projections[0].string).isEqualTo("Test9")
        assertThat(projections[1].string).isEqualTo("Test10")
    }

    @Test
    fun `queryWithProjection{E, P}(predicateBuilder) should return correct projections when subquery is used`() {
        createDefaultTestEntities()

        val projections = entityManager.queryWithProjection<VerySimpleEntity, VerySimpleEntityProjection>(
            predicateBuilder = { cb, query, root ->
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
        )

        assertThat(projections).hasSize(3)
        assertThat(projections[0].int).isEqualTo(3)
        assertThat(projections[1].int).isEqualTo(6)
        assertThat(projections[2].int).isEqualTo(9)
        assertThat(projections[0].string).isEqualTo("Test3")
        assertThat(projections[1].string).isEqualTo("Test6")
        assertThat(projections[2].string).isEqualTo("Test9")
    }
}
