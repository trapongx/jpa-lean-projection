package com.runninglane.jpa.projection.integration.cases.association.manytoone

import javax.persistence.*

@Entity
class ManyToOneUnidirectionalLeftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ManyToOne
    var right: ManyToOneUnidirectionalRightEntity? = null
}