package com.runninglane.jpa.projection.integration.cases.customproperties

import com.runninglane.jpa.projection.annotation.NoProjection

interface CustomProjectionSuperInterface2<T : Any> {
    @NoProjection
    var customString: String
}