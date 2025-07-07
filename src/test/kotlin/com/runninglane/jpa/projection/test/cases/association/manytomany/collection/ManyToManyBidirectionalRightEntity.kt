package com.runninglane.jpa.projection.test.cases.association.manytomany.collection

import javax.persistence.*

@Entity
class ManyToManyBidirectionalRightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var int: Int? = null
    var string: String? = null
    @ManyToMany(mappedBy = "rightList")
    var leftList: List<ManyToManyBidirectionalLeftEntity>? = null
}