package com.runninglane.jpa.projection.test.cases.customproperties

import com.runninglane.jpa.projection.annotations.NoProjection

interface CustomProjectionSuperInterface2<T : Any> {
    @NoProjection
    var customString: String
}