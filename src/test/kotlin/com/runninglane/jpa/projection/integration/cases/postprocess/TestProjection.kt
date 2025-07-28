package com.runninglane.jpa.projection.integration.cases.postprocess

import com.runninglane.jpa.projection.annotation.NoProjection

abstract class TestProjection {
    abstract var id: Long?
    abstract var name: String?

    @NoProjection
    var nameCapitalized: String? = null

    fun postLoad() {
        nameCapitalized = name?.uppercase()
    }
}