package com.runninglane.jpa.projection.integration.cases.tree

import com.runninglane.jpa.projection.integration.BaseTest
import com.runninglane.jpa.projection.mapper.EntityClassMapper
import com.runninglane.jpa.projection.mapper.EntityClassMapperUsingFetcher
import com.runninglane.jpa.projection.mapper.assert.expectEntityClassMapper
import com.runninglane.jpa.projection.mapper.association.AnyToOnePropertyMapperSimplifiedWithJoinFetch
import com.runninglane.jpa.projection.queryWithProjection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [TreeTestConfig::class])
class TreeTest : BaseTest() {

    @Test
    fun `should correctly create mappers`() {
        val mapper = EntityClassMapper.of(
            entityManager.projectorFactory,
            NodeEntity::class,
            NodeProjection::class,
            false,
            null
        )

        val mapperAssertion = expectEntityClassMapper {
            expectSameTypePropertyMapper("id")
            expectMapper("parent", AnyToOnePropertyMapperSimplifiedWithJoinFetch::class) {
                expectMapper(EntityClassMapperUsingFetcher::class) {
                    expectSameTypePropertyMapper("id")
                }
            }
            expectSameTypePropertyMapper("name")
        }

        mapperAssertion.assert(mapper)
    }

    @Test
    fun `should correctly project tree type entity`() {
        val node1 = NodeEntity().apply {
            name = "Node1"
        }.also { entityManager.persist(it) }
        val node2 = NodeEntity().apply {
            name = "Node2"
            parent = node1
        }.also { entityManager.persist(it) }
        val node3 = NodeEntity().apply {
            name = "Node3"
            parent = node2
        }.also { entityManager.persist(it) }
        val node4 = NodeEntity().apply {
            name = "Node4"
            parent = node3
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val node4Projection = entityManager.queryWithProjection<NodeEntity, NodeProjection>(
            predicateBuilder = { cb, _, root -> cb.equal(root.get<Long>("id"), node4.id) }
        ).single()

        assertThat(node4Projection.parent?.name).isEqualTo("Node3")
        assertThat(node4Projection.parent?.parent?.name).isEqualTo("Node2")
        assertThat(node4Projection.parent?.parent?.parent?.name).isEqualTo("Node1")
        assertThat(node4Projection.parent?.parent?.parent?.parent).isNull()
    }

}
