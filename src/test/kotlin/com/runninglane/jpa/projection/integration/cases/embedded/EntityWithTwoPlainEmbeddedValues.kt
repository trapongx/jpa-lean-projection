package com.runninglane.jpa.projection.integration.cases.embedded

import javax.persistence.*

@Entity
class EntityWithTwoPlainEmbeddedValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "int", column = Column(name = "embedded1_int")),
        AttributeOverride(name = "long", column = Column(name = "embedded1_long")),
        AttributeOverride(name = "string", column = Column(name = "embedded1_string"))
    )
    var embedded1: EmbeddableValue = EmbeddableValue()
    
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "int", column = Column(name = "embedded2_int")),
        AttributeOverride(name = "long", column = Column(name = "embedded2_long")),
        AttributeOverride(name = "string", column = Column(name = "embedded2_string"))
    )
    var embedded2: EmbeddableValue? = null
}