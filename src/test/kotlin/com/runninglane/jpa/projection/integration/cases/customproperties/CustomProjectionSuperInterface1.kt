package com.runninglane.jpa.projection.integration.cases.customproperties

import com.runninglane.jpa.projection.annotation.NoProjection

interface CustomProjectionSuperInterface1<T : Any> {
    @NoProjection
    val delegate: T
}