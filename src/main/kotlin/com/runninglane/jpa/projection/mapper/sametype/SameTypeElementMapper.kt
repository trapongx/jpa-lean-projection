package com.runninglane.jpa.projection.mapper.sametype

import com.runninglane.jpa.projection.mapper.Mapper
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path

internal class SameTypeElementMapper(
    parent: Mapper?,
) : SameTypeMapper(parent) {

    override fun buildValueExpression(path: Path<*>): Expression<*> {
        return path
    }

    override fun storeValue(projection: Any, value: Any?) {
        projection as SameTypeElementHolder
        projection.value = value
    }
}
