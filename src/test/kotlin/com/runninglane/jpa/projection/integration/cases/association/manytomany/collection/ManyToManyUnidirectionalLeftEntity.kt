package com.runninglane.jpa.projection.integration.cases.association.manytomany.collection

import javax.persistence.*

@Entity
class ManyToManyUnidirectionalLeftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ManyToMany(cascade = [CascadeType.ALL])
    var rightList: List<ManyToManyUnidirectionalRightEntity>? = null
}