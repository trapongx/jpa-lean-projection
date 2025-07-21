package com.runninglane.jpa.projection.mapper.sametype

import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.reflection.annotatedWith
import com.runninglane.jpa.projection.mapper.Mapper
import com.runninglane.jpa.projection.mapper.PropertyAccessor
import com.runninglane.jpa.projection.mapper.PropertyMapper
import javax.persistence.EmbeddedId
import javax.persistence.Id
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

internal class SameTypePropertyMapper(
    projectorFactory: ProjectorFactory,
    parent: Mapper?,
    private val entityClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    override val propertyName: String
) : SameTypeMapper(parent), PropertyMapper {

    init {
        assert(!projectionClassImpl.isAbstract)
    }

    val isIdProperty: Boolean = run {
        val prop = entityClass.memberProperties.find { it.name == propertyName }
            ?: error("Projected property `$propertyName` not found in class `${projectionClassImpl.qualifiedName}`")

        prop.let { it.annotatedWith<Id>() || it.annotatedWith<EmbeddedId>() }
    }

    private val propertyAccessor = PropertyAccessor.of(projectorFactory.projectionFactory, entityClass, projectionClassImpl, propertyName)

    override fun buildValueExpression(path: Path<*>): Expression<*> {
        return path.get<Any?>(propertyName)
    }

    override fun storeValue(projection: Any, value: Any?) {
        propertyAccessor.set(projection, value)
    }
}
