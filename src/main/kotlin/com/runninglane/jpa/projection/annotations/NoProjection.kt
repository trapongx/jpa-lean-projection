package com.runninglane.jpa.projection.annotations

import java.lang.annotation.Inherited

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Inherited
annotation class NoProjection()
