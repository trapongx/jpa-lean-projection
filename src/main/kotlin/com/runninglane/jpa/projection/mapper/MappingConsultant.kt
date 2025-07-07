package com.runninglane.jpa.projection.mapper

import kotlin.reflect.KClass

interface MappingConsultant {
    fun shouldMap(baseClass: KClass<*>, entityClass: KClass<*>, propertyName: String): Boolean
}