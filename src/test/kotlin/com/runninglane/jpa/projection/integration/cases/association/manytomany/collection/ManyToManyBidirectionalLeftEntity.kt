package com.runninglane.jpa.projection.integration.cases.association.manytomany.collection

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