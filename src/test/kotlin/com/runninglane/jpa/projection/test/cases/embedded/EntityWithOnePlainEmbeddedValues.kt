package com.runninglane.jpa.projection.test.cases.embedded

import javax.persistence.*

@Entity
class EntityWithOnePlainEmbeddedValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Embedded
    var embedded: EmbeddableValue = EmbeddableValue()
}