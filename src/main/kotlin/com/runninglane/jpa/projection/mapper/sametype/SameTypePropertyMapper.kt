package com.runninglane.jpa.projection.mapper.sametype

import com.runninglane.jpa.projection.mapper.Mapper
import com.runninglane.jpa.projection.mapper.PropertyAccessor
import com.runninglane.jpa.projection.mapper.PropertyMapper
import com.runninglane.jpa.projection.reflection.annotatedWith
import javax.persistence.EmbeddedId
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.criteria.Expression
import javax.persistence.criteria.From
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Path
import javax.persistence.criteria.Root
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

internal class SameTypePropertyMapper(
    parent: Mapper?,
    entityClass: KClass<*>,
    projectionClassImpl: KClass<*>,
    override val propertyName: String
) : SameTypeMapper(parent), PropertyMapper {

    init {
        assert(!projectionClassImpl.isAbstract)
    }

    val prop = entityClass.memberProperties.find { it.name == propertyName }
        ?: error("Projected property `$propertyName` not found in class `${projectionClassImpl.qualifiedName}`")

    val isIdProperty: Boolean = prop.let { it.annotatedWith<Id>() || it.annotatedWith<EmbeddedId>() }

    private val isAssociation = prop.let { it.annotatedWith<OneToOne>() || it.annotatedWith<ManyToOne>() }

    private val propertyAccessor = PropertyAccessor.of(entityClass, projectionClassImpl, propertyName)

    override fun buildValueExpression(path: Path<*>): Expression<*> {
        return when {
            isAssociation -> (path as From<*, *>).join<Any, Any>(propertyName, JoinType.LEFT)
            else -> path.get<Any>(propertyName)
        }
    }

    override fun storeValue(projection: Any, value: Any?) {
        propertyAccessor.set(projection, value)
    }
}
