package com.runninglane.jpa.projection.mapper.assert

import com.runninglane.jpa.projection.mapper.Mapper

internal class MapperAssertionError(message: String, cause: Throwable?, val firstErrorNode: Mapper) :
    AssertionError(message, cause)