package com.runninglane.jpa.projection.test

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.jpa.projection.Hydrator
import com.runninglane.jpa.projection.ProjectableEntityManager
import com.runninglane.jpa.projection.mapper.EntityClassMapper
import com.runninglane.jpa.projection.mapper.PropertyAccessor
import com.runninglane.jpa.projection.projectable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import kotlin.reflect.KClass

/**
 * Base class with common utilities for all test cases
 */
@DataJpaTest
@ContextConfiguration
abstract class BaseTest(
    private val dtoBuddy: DtoBuddy? = null
) {

    protected lateinit var entityManager: ProjectableEntityManager

    @PersistenceContext
    protected fun setEntityManager(entityManager: EntityManager) {
        this.entityManager = if (dtoBuddy != null) {
            entityManager.projectable(dtoBuddy)
        } else {
            entityManager.projectable()
        }
    }

    /**
     * Utility function to add context to test failures
     */
    protected fun <T> withContext(context: String, block: () -> T): T {
        try {
            return block()
        } catch (e: AssertionError) {
            throw AssertionError("$context: ${e.message}", e)
        }
    }

    protected fun <E: Any, P : Any> projectAndVerify(
        entityClass: KClass<E>,
        projectionClass: KClass<P>,
        expectedProjectionCount: Int,
        entityManagerWithCounter: EntityManagerWithCounter? = null,
        predicateBuilder: ((CriteriaBuilder, CriteriaQuery<*>, Root<*>) -> Predicate?)? = null,
        verify: (List<E>, List<P>) -> Unit
    ) {
        // Arrange
        val entities = run {
            val cb = entityManager.criteriaBuilder
            val query = cb.createQuery(entityClass.java)
            val root = query.from(entityClass.java)
            query.select(root)
            predicateBuilder?.invoke(cb, query, root)?.also { predicate -> query.where(predicate) }
            entityManager.createQuery(query).resultList
        }

        // Act
        val projections = (entityManagerWithCounter ?: entityManager)
            .queryWithProjection(entityClass, projectionClass, predicateBuilder)

        // Assert
        assertThat(projections).hasSize(expectedProjectionCount)
        assertThat(entities).hasSize(expectedProjectionCount)
        verify(entities, projections)
    }

    protected fun <E: Any, P : Any> projectAndVerifyEach(
        entityClass: KClass<E>,
        projectionClass: KClass<P>,
        getEntityId: (E) -> Long?,
        getProjectionId: (P) -> Long?,
        expectedProjectionCount: Int,
        entityManagerWithCounter: EntityManagerWithCounter? = null,
        verify: (E, P) -> Unit
    ) {
        projectAndVerify(
            entityClass,
            projectionClass,
            expectedProjectionCount,
            entityManagerWithCounter
        ) { entities, projections ->
            verifyEach(entities, projections, getEntityId, getProjectionId, verify)
        }
    }

    /**
     * Verify each entity matches its projection
     */
    protected fun <E: Any, P : Any> verifyEach(
        entities: List<E>,
        projections: List<P>,
        getEntityId: (E) -> Long?,
        getProjectionId: (P) -> Long?,
        verify: (E, P) -> Unit
    ) {
        entities.forEach { entity ->
            withContext("Verifying projection for entity ID ${getEntityId(entity)}") {
                val projection = projections.find { getProjectionId(it) == getEntityId(entity) }
                    ?: throw AssertionError("Projection not found for entity ID ${getEntityId(entity)}")
                verify(entity, projection)
            }
        }
    }

    protected fun <E: Any, P : Any, T : Throwable> projectAndExpectException(
        entityClass: KClass<E>,
        projectionClass: KClass<P>,
        throwableClass: KClass<T>,
        entityManagerWithCounter: EntityManagerWithCounter? = null
    ) {
        // Act, Assert
        Assertions.assertThrows(throwableClass.java) {
            (entityManagerWithCounter ?: entityManager)
                .queryWithProjection(entityClass, projectionClass)
        }
    }
}
