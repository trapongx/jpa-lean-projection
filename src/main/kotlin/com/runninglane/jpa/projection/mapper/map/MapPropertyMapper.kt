package com.runninglane.jpa.projection.mapper.map

import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.Mapper
import com.runninglane.jpa.projection.mapper.PropertyInfo
import com.runninglane.jpa.projection.mapper.PropertyMapper
import com.runninglane.jpa.projection.mapper.SimplifiableMapper
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

internal class MapPropertyMapper(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    private val propertyInfo: PropertyInfo,
    hadJoinFetch: Boolean
) : SimplifiableMapper, PropertyMapper {

    init {
        assert(
            projectionClass != projectionClassImpl
                    && !projectionClassImpl.isAbstract
                    && projectionClassImpl.isSubclassOf(projectionClass)
        )
    }

    override val propertyName: String get() = propertyInfo.propertyName

    private val useJoinFetch = !hadJoinFetch
            && propertyInfo.propTypeArgType1 == propertyInfo.srcPropTypeArgType1
            && propertyInfo.propTypeArgType2 == propertyInfo.srcPropTypeArgType2

    override fun simplify(): Mapper = when {
        useJoinFetch -> MapPropertyMapperSimplifiedWithJoinFetch(
            projectorFactory,
            parent,
            propertyInfo
        )
        else -> MapPropertyMapperUsingFetcher(
            projectorFactory,
            parent,
            entityClass,
            projectionClass,
            projectionClassImpl,
            propertyInfo
        )
    }
}