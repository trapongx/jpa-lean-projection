package com.runninglane.jpa.projection.integration.cases.customproperties

import com.runninglane.jpa.projection.annotations.NoProjection

interface CustomProjectionSuperInterface1<T : Any> {
    @NoProjection
    val delegate: T
}