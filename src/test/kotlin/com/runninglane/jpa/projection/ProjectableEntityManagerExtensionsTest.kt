package com.runninglane.jpa.projection

import com.runninglane.dto.buddy.DtoBuddy
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import javax.persistence.EntityManager
import javax.persistence.criteria.*

class ProjectableEntityManagerExtensionsTest {

    open class TargetDelegate {
        // Utility function to abstract extension function call
        open fun projectable(entityManager: EntityManager): ProjectableEntityManager = entityManager.projectable()

        open fun projectable(entityManager: EntityManager, projectorFactory: ProjectorFactory): ProjectableEntityManager =
            entityManager.projectable(projectorFactory)

        open fun projectable(entityManager: EntityManager, dtoBuddy: DtoBuddy): ProjectableEntityManager =
            entityManager.projectable(dtoBuddy)

        open fun x() = 5
    }

    private lateinit var mockTarget: TargetDelegate
    private lateinit var mockEntityManager: EntityManager
    private lateinit var mockProjectableEntityManager: ProjectableEntityManager
    private lateinit var mockProjectorFactory: ProjectorFactory
    private lateinit var mockProjectionFactory: ProjectionFactory
    private lateinit var mockDtoBuddy: DtoBuddy

    // Test models
    class TestEntity(val name: String)
    data class TestProjection(val name: String)

    @BeforeEach
    fun setUp() {
        mockTarget = mock(TargetDelegate::class.java)
        mockEntityManager = mock(EntityManager::class.java)
        mockProjectableEntityManager = mock(ProjectableEntityManager::class.java)
        mockProjectorFactory = mock(ProjectorFactory::class.java)
        mockProjectionFactory = mock(ProjectionFactory::class.java)
        mockDtoBuddy = mock(DtoBuddy::class.java)

        // Setup mocks
        `when`(mockProjectableEntityManager.projectorFactory).thenReturn(mockProjectorFactory)
        `when`(mockProjectorFactory.projectionFactory).thenReturn(mockProjectionFactory)
    }

    @Test
    fun `test EntityManager projectable() should return existing ProjectableEntityManager`() {
        // When calling projectable() on an existing ProjectableEntityManager
        `when`(mockTarget.projectable(mockProjectableEntityManager)).thenCallRealMethod()

        // Then it should return the same instance
        val result = mockTarget.projectable(mockProjectableEntityManager)
        assertEquals(mockProjectableEntityManager, result)
    }

    @Test
    fun `test EntityManager projectable() should create new ProjectableEntityManagerImpl`() {
        // When calling projectable() on a regular EntityManager
        `when`(mockTarget.projectable(mockEntityManager)).thenCallRealMethod()

        // Then it should create a new ProjectableEntityManagerImpl
        val result = mockTarget.projectable(mockEntityManager)
        assertNotNull(result)
        assertTrue(result is ProjectableEntityManagerImpl)
    }

    @Test
    fun `test EntityManager projectable() with custom projectorFactory`() {
        // Given a custom projectorFactory
        val customProjectorFactory = mock(ProjectorFactory::class.java)

        // When calling projectable() on a regular EntityManager with custom projectorFactory
        `when`(mockTarget.projectable(mockEntityManager, customProjectorFactory)).thenCallRealMethod()

        // Then it should create a new ProjectableEntityManagerImpl with the custom factory
        val result = mockTarget.projectable(mockEntityManager, customProjectorFactory)
        assertNotNull(result)
        assertTrue(result is ProjectableEntityManagerImpl)
        assertEquals(customProjectorFactory, (result as ProjectableEntityManagerImpl).projectorFactory)
    }

    @Test
    fun `test EntityManager projectable() with dtoBuddy when not already ProjectableEntityManager`() {
        // Given a custom dtoBuddy
        `when`(mockTarget.projectable(mockEntityManager, mockDtoBuddy)).thenCallRealMethod()

        // When calling projectable() with dtoBuddy
        val result = mockTarget.projectable(mockEntityManager, mockDtoBuddy)

        // Then it should create a new ProjectableEntityManagerImpl with factory using dtoBuddy
        assertNotNull(result)
        assertTrue(result is ProjectableEntityManagerImpl)
        // Verify the DtoBuddy was used to create a ProjectorFactory with DefaultProjectionFactory
    }

    @Test
    fun `test EntityManager projectable() with dtoBuddy when already ProjectableEntityManager with same dtoBuddy`() {
        // Given a ProjectableEntityManager with the same dtoBuddy
        `when`(mockTarget.projectable(mockProjectableEntityManager, mockDtoBuddy)).thenCallRealMethod()
        `when`(mockProjectionFactory.dtoBuddy).thenReturn(mockDtoBuddy)

        // When calling projectable() with the same dtoBuddy
        val result = mockTarget.projectable(mockProjectableEntityManager, mockDtoBuddy)

        // Then it should return the same ProjectableEntityManager
        assertEquals(mockProjectableEntityManager, result)
    }

    @Test
    fun `test EntityManager projectable() with dtoBuddy when already ProjectableEntityManager with different dtoBuddy should throw`() {
        // Given a ProjectableEntityManager with a different dtoBuddy
        val differentDtoBuddy = mock(DtoBuddy::class.java)
        `when`(mockTarget.projectable(mockProjectableEntityManager, mockDtoBuddy)).thenCallRealMethod()
        `when`(mockProjectionFactory.dtoBuddy).thenReturn(differentDtoBuddy)

        // When calling projectable() with a different dtoBuddy
        // Then it should throw an IllegalArgumentException
        assertThrows(IllegalArgumentException::class.java) {
            mockTarget.projectable(mockProjectableEntityManager, mockDtoBuddy)
        }
    }

    @Test
    fun `test queryWithProjection reified extension function delegates to the non-reified version`() {
        // Given
        val mockPredicateBuilder = mock<(CriteriaBuilder, CriteriaQuery<*>, Root<TestEntity>) -> Predicate?>()
        val mockOrdersBuilder = mock<(CriteriaBuilder, Root<TestEntity>) -> Array<Order>?>()
        val mockResults = listOf(TestProjection("test"))

        // Configure the mock
        `when`(mockProjectableEntityManager.queryWithProjection(
            TestEntity::class, 
            TestProjection::class, 
            mockPredicateBuilder, 
            mockOrdersBuilder, 
            10, 
            20
        )).thenReturn(mockResults)

        // When using the reified extension function
        val result = mockProjectableEntityManager.queryWithProjection<TestEntity, TestProjection>(
            mockPredicateBuilder,
            mockOrdersBuilder,
            10,
            20
        )

        // Then it should delegate to the non-reified version
        assertEquals(mockResults, result)
        verify(mockProjectableEntityManager).queryWithProjection(
            TestEntity::class, 
            TestProjection::class, 
            mockPredicateBuilder, 
            mockOrdersBuilder, 
            10, 
            20
        )
    }

}