package com.runninglane.jpa.projection.test.cases.equality

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeGenBasedByteCodeStrategy
import com.runninglane.jpa.projection.ProjectionCodeGenerator
import com.runninglane.jpa.projection.ProjectionFactory
import com.runninglane.jpa.projection.test.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [EqualityTestConfig::class])
class EqualityTest : BaseTest() {

    @Test
    fun `should calculate entity equality based on their ID correctly`() {
        val projectionFactory = ProjectionFactory()
        val projection1 = projectionFactory.create(TestEntity::class, TestEntityProjection::class)
            .also {
                projectionFactory.dtoBuddy.populate(it, mapOf("id" to 1L, "name" to "Test1"))
            }
        val projection2 = projectionFactory.create(TestEntity::class, TestEntityProjection::class)
            .also {
                projectionFactory.dtoBuddy.populate(it, mapOf("id" to 1L, "name" to "Test2"))
            }
        val projection3 = projectionFactory.create(TestEntity::class, TestEntityProjection::class)
            .also {
                projectionFactory.dtoBuddy.populate(it, mapOf("id" to 2L, "name" to "Test2"))
            }

        assertThat(projection1).isEqualTo(projection2)
        assertThat(projection1.hashCode()).isEqualTo(projection2.hashCode())
        assertThat(projection1).isNotEqualTo(projection3)
        assertThat(projection1.hashCode()).isNotEqualTo(projection3.hashCode())
        assertThat(projection2).isNotEqualTo(projection3)
        assertThat(projection2.hashCode()).isNotEqualTo(projection3.hashCode())
    }

    @Test
    fun `should calculate embeddable equality correctly when no explicit ID is defined and strategy set to not override`() {
        val projectionFactory = ProjectionFactory(
            DtoBuddy(
                KotlinCodeGenBasedByteCodeStrategy(ProjectionCodeGenerator(
                    ProjectionCodeGenerator.NoIdsStrategy.DONT_OVERRIDE
                ))
            )
        )
        val projection1 = projectionFactory.create(TestEmbeddable::class, TestEmbeddableProjection::class)
            .also {
                projectionFactory.dtoBuddy.populate(it, mapOf("score" to 1, "name" to "Test1"))
            }
        val projection2 = projectionFactory.create(TestEmbeddable::class, TestEmbeddableProjection::class)
            .also {
                projectionFactory.dtoBuddy.populate(it, mapOf("score" to 1, "name" to "Test1"))
            }
        val projection3 = projectionFactory.create(TestEmbeddable::class, TestEmbeddableProjection::class)
            .also {
                projectionFactory.dtoBuddy.populate(it, mapOf("score" to 1, "name" to "Test2"))
            }

        assertThat(projection1).isNotEqualTo(projection2)
        assertThat(projection1.hashCode()).isNotEqualTo(projection2.hashCode())
        assertThat(projection1).isNotEqualTo(projection3)
        assertThat(projection1.hashCode()).isNotEqualTo(projection3.hashCode())
        assertThat(projection2).isNotEqualTo(projection3)
        assertThat(projection2.hashCode()).isNotEqualTo(projection3.hashCode())
    }

    @Test
    fun `should calculate embeddable equality correctly when no explicit ID is defined and strategy set to use all properties as IDs`() {
        val projectionFactory = ProjectionFactory(
            DtoBuddy(
                KotlinCodeGenBasedByteCodeStrategy(ProjectionCodeGenerator(
                    ProjectionCodeGenerator.NoIdsStrategy.USE_ALL_PROPERTIES_AS_IDS
                ))
            )
        )
        val projection1 = projectionFactory.create(TestEmbeddable2::class, TestEmbeddableProjection2::class)
            .also {
                projectionFactory.dtoBuddy.populate(it, mapOf("score" to 1, "name" to "Test1"))
            }
        val projection2 = projectionFactory.create(TestEmbeddable2::class, TestEmbeddableProjection2::class)
            .also {
                projectionFactory.dtoBuddy.populate(it, mapOf("score" to 1, "name" to "Test1"))
            }
        val projection3 = projectionFactory.create(TestEmbeddable2::class, TestEmbeddableProjection2::class)
            .also {
                projectionFactory.dtoBuddy.populate(it, mapOf("score" to 1, "name" to "Test2"))
            }

        assertThat(projection1).isEqualTo(projection2)
        assertThat(projection1.hashCode()).isEqualTo(projection2.hashCode())
        assertThat(projection1).isNotEqualTo(projection3)
        assertThat(projection1.hashCode()).isNotEqualTo(projection3.hashCode())
        assertThat(projection2).isNotEqualTo(projection3)
        assertThat(projection2.hashCode()).isNotEqualTo(projection3.hashCode())
    }
}
