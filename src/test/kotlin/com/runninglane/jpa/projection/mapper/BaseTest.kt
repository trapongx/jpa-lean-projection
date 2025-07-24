package com.runninglane.jpa.projection.mapper

import com.runninglane.jpa.projection.ProjectionFactory
import com.runninglane.jpa.projection.ProjectorFactory
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.reflect.KClass

internal abstract class BaseTest {

    fun createEntityClassMapperWithMocks(
        entityClass: KClass<*>,
        projectionClass: KClass<*>,
        mockProjectionClassImpl: KClass<*>,
        mockProjectionInstance: Any,
        mappings: Map<Pair<KClass<*>, KClass<*>>, Pair<KClass<*>, () -> Any>>? = null
    ): EntityClassMapper = createEntityClassMapperWithMocks(
        entityClass, projectionClass,
        mapOf(Pair(
            (entityClass to projectionClass),
            (mockProjectionClassImpl to { mockProjectionInstance })
        )) + (mappings ?: emptyMap())
    )

    fun createEntityClassMapperWithMocks(
        entityClass: KClass<*>,
        projectionClass: KClass<*>,
        mappings: Map<Pair<KClass<*>, KClass<*>>, Pair<KClass<*>, () -> Any>>? = null
    ): EntityClassMapper {
        // Given
        val projectionFactory = mock(ProjectionFactory::class.java)
        val projectorFactory = mock(ProjectorFactory::class.java)
        `when`(projectorFactory.projectionFactory).thenReturn(projectionFactory)

        // Mock the projection class implementation
        // This works because the methods in ProjectionFactory are marked as 'open'
        // Without 'open', Mockito can't override the methods and will call the real implementation
        mappings?.forEach { (key, value) ->
            val (mockProjectionClassImpl, mockProjectionInstance) = value
            val (entityClass, projectionClass) = key

            `when`(projectionFactory.getImplementation(entityClass, projectionClass))
                .thenReturn(mockProjectionClassImpl)

            `when`(projectionFactory.create(entityClass, projectionClass))
                .thenReturn(mockProjectionInstance)
        }

        return EntityClassMapper.of(
            projectorFactory,
            null,
            entityClass,
            projectionClass,
            false,
            null
        ) as EntityClassMapper
    }
}