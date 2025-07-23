package com.runninglane.jpa.projection.integration.cases.embedded

import javax.persistence.*

@Entity
class EntityWithOuterEmbeddedValueWithTwoInner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Embedded
    var embedded: OuterEmbeddableValueWithTwoInner = OuterEmbeddableValueWithTwoInner()
}