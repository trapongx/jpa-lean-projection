package com.runninglane.jpa.projection.integration.cases.elementcollection.collection

import javax.persistence.Embeddable

/**
 * The suffix `Value` is added to avoid conflict with the annotation class name
 */
@Embeddable
data class EmbeddableValue(
    var short: Short = 0,
    var double: Double = 0.0,
    var string: String = ""
)