package com.runninglane.jpa.projection.integration.cases.association.onetoone

import javax.persistence.*

@Entity
class OneToOneBidirectionalRightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var int: Int? = null
    var string: String? = null
    @OneToOne(mappedBy = "right")
    var left: OneToOneBidirectionalLeftEntity? = null
}