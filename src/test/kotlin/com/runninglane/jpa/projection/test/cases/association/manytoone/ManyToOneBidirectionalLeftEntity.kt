package com.runninglane.jpa.projection.test.cases.association.manytoone

import javax.persistence.*

@Entity
class ManyToOneBidirectionalLeftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ManyToOne
    var right: ManyToOneBidirectionalRightEntity? = null
}