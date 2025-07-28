package com.runninglane.jpa.projection.integration.cases.postprocess

import com.runninglane.jpa.projection.ProjectableEntityManager
import com.runninglane.jpa.projection.integration.BaseTest
import com.runninglane.jpa.projection.integration.cases.simplevalue.SimpleValueTest.InnerDepth1.ProjectionAsInnerOfInnerInterface
import com.runninglane.jpa.projection.projectable
import com.runninglane.jpa.projection.queryWithProjection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@DataJpaTest
@ContextConfiguration(classes = [PostProcessTestConfig::class])
class PostProcessTest {

    private lateinit var entityManager: ProjectableEntityManager

    @PersistenceContext
    fun setEntityManager(entityManager: EntityManager) {
        this.entityManager = entityManager.projectable(
            projectionPostProcessor = { projection ->
                when (projection) {
                    is TestProjection -> projection.postLoad()
                }
            }
        )
    }

    @Test
    fun `should correctly call post processor`() {
        val entity = TestEntity().apply {
            name = "Test1"
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val projection = entityManager.queryWithProjection<TestEntity, TestProjection>().single()
        assertThat(projection.id).isEqualTo(entity.id)
        assertThat(projection.name).isEqualTo("Test1")
        assertThat(projection.nameCapitalized).isEqualTo("TEST1")
    }

}
