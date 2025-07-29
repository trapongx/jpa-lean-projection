package com.runninglane.jpa.projection.integration.cases.treeusingmap

import com.runninglane.jpa.projection.integration.BaseTest
import com.runninglane.jpa.projection.mapper.EntityClassMapper
import com.runninglane.jpa.projection.mapper.assert.expectEntityClassMapper
import com.runninglane.jpa.projection.mapper.map.MapPropertyMapperUsingFetcher
import com.runninglane.jpa.projection.queryWithProjection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [TreeUsingMapTestConfig::class])
class TreeUsingMapTest : BaseTest() {

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
            expectMapper("children", MapPropertyMapperUsingFetcher::class)
            expectSameTypePropertyMapper("name")
        }

        mapperAssertion.assert(mapper)
    }

    @Test
    fun `should correctly project tree type entity having 2 directions nodes`() {
        val node1 = NodeEntity().apply {
            name = "Node1"
        }.also { entityManager.persist(it) }
        val node2 = NodeEntity().apply {
            name = "Node2"
            children = mutableMapOf(node1 to "default")
        }.also { entityManager.persist(it) }
        val node3 = NodeEntity().apply {
            name = "Node3"
            children = mutableMapOf(node2 to "default")
        }.also { entityManager.persist(it) }
        val node4 = NodeEntity().apply {
            name = "Node4"
            children = mutableMapOf(node3 to "default")
        }.also { entityManager.persist(it) }
        node1.children = mutableMapOf(node4 to "default")
        entityManager.merge(node1)

        entityManager.flush()
        entityManager.clear()

        val node4Projection = entityManager.queryWithProjection<NodeEntity, NodeProjection>(
            predicateBuilder = { cb, _, root -> cb.equal(root.get<Long>("id"), node4.id) }
        ).single()

        assertThat(node4Projection.children?.keys?.singleOrNull()?.name).isEqualTo("Node3")
        assertThat(node4Projection.children?.keys?.singleOrNull()?.children?.keys?.singleOrNull()?.name).isEqualTo("Node2")
        assertThat(node4Projection.children?.keys?.singleOrNull()?.children?.keys?.singleOrNull()?.children?.keys?.singleOrNull()?.name).isEqualTo("Node1")
        assertThat(node4Projection.children?.keys?.singleOrNull()?.children?.keys?.singleOrNull()?.children?.keys?.singleOrNull()?.children?.keys?.singleOrNull()?.name).isEqualTo("Node4")
    }

}
