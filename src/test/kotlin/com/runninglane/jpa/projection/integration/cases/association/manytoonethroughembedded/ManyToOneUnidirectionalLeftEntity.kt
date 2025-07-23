package com.runninglane.jpa.projection.integration.cases.association.manytoonethroughembedded
import javax.persistence.*

@Entity
class ManyToOneUnidirectionalLeftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Embedded
    var right: ManyToOneUnidirectionalRightEmbeddable? = null
}