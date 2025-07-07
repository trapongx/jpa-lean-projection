package com.runninglane.jpa.projection.test.cases.embedded

import javax.persistence.*

@Embeddable
data class OuterEmbeddableValueWithTwoInner(
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "int", column = Column(name = "inner1_int")),
        AttributeOverride(name = "long", column = Column(name = "inner1_long")),
        AttributeOverride(name = "string", column = Column(name = "inner1_string"))
    )
    var inner1: EmbeddableValue = EmbeddableValue(),

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "int", column = Column(name = "inner2_int")),
        AttributeOverride(name = "long", column = Column(name = "inner2_long")),
        AttributeOverride(name = "string", column = Column(name = "inner2_string"))
    )
    var inner2: EmbeddableValue? = null
)