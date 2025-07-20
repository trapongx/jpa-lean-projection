package com.runninglane.jpa.projection.test.cases.customproperties

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeGenBasedByteCodeStrategy
import com.runninglane.jpa.projection.ProjectionFactory
import com.runninglane.jpa.projection.queryWithProjection
import com.runninglane.jpa.projection.test.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.test.assertTrue
import kotlin.test.fail

@DataJpaTest
@ContextConfiguration(classes = [CustomPropertiesTestConfiguration::class])
class CustomPropertiesTest : BaseTest(
    DtoBuddy(KotlinCodeGenBasedByteCodeStrategy(CustomProjectionCodeGenerator()))
) {

    private val projectionFactory: ProjectionFactory
        get() = entityManager.projectorFactory.projectionFactory

    @Test
    fun `should correctly recognize and populate projection superinterfaces with custom property of type entity`() {
        val implClass = projectionFactory
            .getImplementation(TestEntity::class, Projection1::class)

        assertTrue { implClass.isSubclassOf(Projection1::class) }

        val prop = implClass.memberProperties.single { it.name == "delegate" }

        assertTrue { prop.returnType.classifier == TestEntity::class }

        val entity = TestEntity().apply {
            name = "Test"
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val projection = entityManager.queryWithProjection<TestEntity, Projection1>().single()

        assertThat(projection.id).isEqualTo(entity.id)
        assertThat(projection.name).isEqualTo("Test")
        runCatching {
            projection.delegate
        }.onSuccess {
            fail("Should throw exception because delegate property is TODO(\"id = \$id\")")
        }.onFailure {
            assertThat(it).isInstanceOf(NotImplementedError::class.java)
            assertThat(it.message).contains("id = ${projection.id}")
        }
    }

    @Test
    fun `should correctly recognize and populate projection superinterfaces with custom property of type string`() {
        val implClass = projectionFactory
            .getImplementation(TestEntity::class, Projection2::class)

        assertTrue { implClass.isSubclassOf(Projection2::class) }

        val prop = implClass.memberProperties.single { it.name == "customString" }

        assertTrue { prop.returnType.classifier == String::class }

        val entity = TestEntity().apply {
            name = "Test"
        }.also { entityManager.persist(it) }

        entityManager.flush()
        entityManager.clear()

        val projection = entityManager.queryWithProjection<TestEntity, Projection2>().single()

        assertThat(projection.id).isEqualTo(entity.id)
        assertThat(projection.name).isEqualTo("Test")
        assertThat(projection.customString).isEqualTo("Hello")
        projection.customString = "Sawasdee"
        assertThat(projection.customString).isEqualTo("Sawasdee")
    }


}
