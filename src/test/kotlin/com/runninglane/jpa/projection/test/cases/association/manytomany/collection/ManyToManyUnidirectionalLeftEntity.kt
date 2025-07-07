package com.runninglane.jpa.projection.test.cases.association.manytomany.collection

import javax.persistence.*

@Entity
class ManyToManyUnidirectionalLeftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ManyToMany(cascade = [CascadeType.ALL])
    var rightList: List<ManyToManyUnidirectionalRightEntity>? = null
}