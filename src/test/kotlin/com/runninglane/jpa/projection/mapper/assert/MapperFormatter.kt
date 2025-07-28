package com.runninglane.jpa.projection.mapper.assert

import com.runninglane.jpa.projection.mapper.EntityClassMapper
import com.runninglane.jpa.projection.mapper.Mapper
import com.runninglane.jpa.projection.mapper.PropertyMapper
import com.runninglane.jpa.projection.mapper.embedded.EmbeddableClassMapper

internal object MapperFormatter {
    fun formatTree(mapper: Mapper, level: Int, firstErrorNode: Mapper, errorType: MapperAssertionError.Type): String {
        return buildString {
            append("--".repeat(level))
            append(if (mapper == firstErrorNode) "[${errorType.code}]-" else "[+]-")
            appendLine(format(mapper))
            mapper.getChildren().forEach {
                append(formatTree(it, level + 1, firstErrorNode, errorType))
            }
        }
    }

    fun format(mapper: Mapper): String {
        return buildString {
            append(mapper::class.simpleName)
            if (mapper is PropertyMapper) {
                append("[${mapper.propertyName}]")
            }
            if (mapper is EntityClassMapper) {
                append("[${mapper.entityClass.simpleName}->${mapper.projectionClass.simpleName}->${mapper.projectionClassImpl.simpleName}]")
            } else if (mapper is EmbeddableClassMapper) {
                append("[${mapper.embeddableClass.simpleName}->${mapper.projectionClass.simpleName}->${mapper.projectionClassImpl.simpleName}]")
            }
        }
    }
}