package com.runninglane.jpa.projection

internal data class HydrationMaterial(
    val projection: Any,
    val parentProjection: Any?
)