package com.runninglane.jpa.projection.mapper

import javax.persistence.Tuple
import kotlin.reflect.KClass

internal interface BaseEntityClassMapper : Mapper {
    val projectionClassImpl: KClass<*>
    fun readId(tuple: Tuple): Any?
    fun isIdNull(tuple: Tuple): Boolean
}