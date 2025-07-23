package com.runninglane.jpa.projection.integration.cases.association.onetoone

import javax.persistence.*

@Entity
class OneToOneUnidirectionalLeftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @OneToOne(cascade = [CascadeType.ALL])
    var right: OneToOneUnidirectionalRightEntity? = null
}