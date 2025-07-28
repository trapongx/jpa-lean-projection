package com.runninglane.jpa.projection.mapper.assert

import com.runninglane.jpa.projection.mapper.Mapper

internal class MapperAssertionError(message: String, cause: Throwable?, val firstErrorNode: Mapper, val type: Type) : AssertionError(message, cause) {
    enum class Type(val code: String) {
        MISSING_CHILDREN("M"),
        UNEXPECTED("U"),
        ERROR("E")
    }
}