package com.runninglane.jpa.projection.test.cases.association.onetomany.collection

import javax.persistence.*

@Entity
class OneToManyBidirectionalRightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var int: Int? = null
    var string: String? = null
    @ManyToOne
    var left: OneToManyBidirectionalLeftEntity? = null
}