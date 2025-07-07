package com.runninglane.jpa.projection.test.cases.association.manytomany.collection

import javax.persistence.*

@Entity
class ManyToManyBidirectionalLeftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Suppress("UNUSED")
    @ManyToMany
    var rightList: List<ManyToManyBidirectionalRightEntity>? = null
}