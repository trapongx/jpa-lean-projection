package com.runninglane.jpa.projection.integration.cases.association.manytoonethroughembedded

import javax.persistence.*

@Entity
class ManyToOneBidirectionalLeftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Embedded
    var right: ManyToOneBidirectionalRightEmbeddable? = null
}