package com.runninglane.jpa.projection.integration.cases.embedded

import javax.persistence.Embeddable
import javax.persistence.Embedded

@Embeddable
data class OuterEmbeddableValueWithOneInner(
    @Embedded
    var inner: EmbeddableValue = EmbeddableValue()
)