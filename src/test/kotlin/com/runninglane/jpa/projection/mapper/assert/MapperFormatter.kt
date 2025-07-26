package com.runninglane.jpa.projection.mapper.assert

import com.runninglane.jpa.projection.mapper.EntityClassMapper
import com.runninglane.jpa.projection.mapper.Mapper
import com.runninglane.jpa.projection.mapper.PropertyMapper
import com.runninglane.jpa.projection.mapper.embedded.EmbeddableClassMapper

internal object MapperFormatter {
    fun formatTree(mapper: Mapper, level: Int, firstErrorNode: Mapper): String {
        return buildString {
            append("--".repeat(level))
            append(if (mapper == firstErrorNode) "[E]-" else "[+]-")
            appendLine(format(mapper))
            mapper.getChildren().forEach {
                append(formatTree(it, level + 1, firstErrorNode))
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