package com.runninglane.jpa.projection.mapper

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal data class PropertyInfo(
    val property: KProperty1<out Any, *>,
    val propertyName: String,
    val propertyType: KClass<*>,
    val accessor: PropertyAccessor,
    val isCollection: Boolean,
    val isList: Boolean,
    val isSet: Boolean,
    val isMap: Boolean,
    val isMutable: Boolean,
    val propTypeArgType1: KClass<*>? = null,
    val propTypeArgType2: KClass<*>? = null,
    val srcPropTypeArgType1: KClass<*>? = null,
    val srcPropTypeArgType2: KClass<*>? = null,
)