package com.runninglane.jpa.projection.integration.cases.association.onetomany.collection

import javax.persistence.*

@Entity
class OneToManyBidirectionalLeftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Suppress("UNUSED")
    @OneToMany(mappedBy = "left")
    var rightList: List<OneToManyBidirectionalRightEntity>? = null
}