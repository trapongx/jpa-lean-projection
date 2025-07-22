package com.runninglane.jpa.projection.test.cases.contract

import com.runninglane.jpa.projection.ProjectionFactory
import com.runninglane.jpa.projection.test.BaseTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [ContractTestConfig::class])
class ProjectionFactoryContractTest : BaseTest() {

    val projectionFactory: ProjectionFactory
        get() = entityManager.projectorFactory.projectionFactory

    @Test
    fun `getImplementation(entityClass, projectionClass) should fail when entityClass == projectionClass`() {
        assertThrows<IllegalArgumentException> {
            projectionFactory.getImplementation(DummyEntity::class, DummyEntity::class)
        }
    }

}