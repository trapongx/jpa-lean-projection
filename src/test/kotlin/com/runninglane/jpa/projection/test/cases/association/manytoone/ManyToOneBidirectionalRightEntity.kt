package com.runninglane.jpa.projection.test.cases.association.manytoone

import javax.persistence.*

@Entity
class ManyToOneBidirectionalRightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var int: Int? = null
    var string: String? = null
    @Suppress("UNUSED")
    @OneToMany(mappedBy = "right")
    var leftList: List<ManyToOneBidirectionalLeftEntity>? = null
}