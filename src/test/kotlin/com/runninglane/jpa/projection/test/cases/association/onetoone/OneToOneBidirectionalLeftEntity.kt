package com.runninglane.jpa.projection.test.cases.association.onetoone

import javax.persistence.*

@Entity
class OneToOneBidirectionalLeftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @OneToOne(cascade = [CascadeType.ALL])
    var right: OneToOneBidirectionalRightEntity? = null
}