package com.runninglane.jpa.projection.integration.cases.embedded

import javax.persistence.*

@Entity
class EntityWithOuterEmbeddedValueWithOneInner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Embedded
    var embedded: OuterEmbeddableValueWithOneInner = OuterEmbeddableValueWithOneInner()
}