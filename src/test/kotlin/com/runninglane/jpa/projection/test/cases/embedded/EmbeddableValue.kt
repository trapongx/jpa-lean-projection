package com.runninglane.jpa.projection.test.cases.embedded

import javax.persistence.Embeddable

/**
 * The suffix `Value` is added to avoid conflict with the annotation class name
 */
@Embeddable
data class EmbeddableValue(
    var int: Int? = null,
    var long: Long? = null,
    var string: String = ""
)