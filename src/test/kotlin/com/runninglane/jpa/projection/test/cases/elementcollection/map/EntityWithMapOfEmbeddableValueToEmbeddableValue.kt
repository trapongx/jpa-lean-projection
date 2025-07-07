package com.runninglane.jpa.projection.test.cases.elementcollection.map

import javax.persistence.*

@Entity
class EntityWithMapOfEmbeddableValueToEmbeddableValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ElementCollection
    @AttributeOverrides(
        AttributeOverride(name = "key.short", column = Column(name = "key_short")),
        AttributeOverride(name = "key.double", column = Column(name = "key_double")),
        AttributeOverride(name = "key.string", column = Column(name = "key_string")),
        AttributeOverride(name = "value.short", column = Column(name = "value_short")),
        AttributeOverride(name = "value.double", column = Column(name = "value_double")),
        AttributeOverride(name = "value.string", column = Column(name = "value_string"))
    )
    var elements: Map<EmbeddableValue, EmbeddableValue>? = null
}