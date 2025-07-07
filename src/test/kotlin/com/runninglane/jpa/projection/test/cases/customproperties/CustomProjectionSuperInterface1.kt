package com.runninglane.jpa.projection.test.cases.customproperties

import com.runninglane.jpa.projection.annotations.NoProjection

interface CustomProjectionSuperInterface1<T : Any> {
    @NoProjection
    val delegate: T
}