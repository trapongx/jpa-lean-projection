package com.runninglane.jpa.projection.mapper

import javax.persistence.Tuple

internal interface BaseEntityClassMapper : Mapper {
    fun readId(tuple: Tuple): Any?
    fun isIdNull(tuple: Tuple): Boolean
}