package com.runninglane.jpa.projection.mapper.map

import com.runninglane.jpa.projection.mapper.Mapper
import com.runninglane.jpa.projection.mapper.sametype.SameTypeMapper
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path

internal class SameTypeMapKeyOrValueMapper(
    parent: Mapper?,
    private val isForKey: Boolean
) : SameTypeMapper(parent) {

    override fun buildValueExpression(path: Path<*>): Expression<*> {
        return path
    }

    override fun storeValue(projection: Any, value: Any?) {
        projection as MapEntry
        if (isForKey) {
            projection.key = value
        } else {
            projection.value = value
        }
    }
}
