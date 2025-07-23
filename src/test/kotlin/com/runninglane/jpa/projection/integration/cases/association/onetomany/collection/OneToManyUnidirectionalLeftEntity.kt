package com.runninglane.jpa.projection.integration.cases.association.onetomany.collection

import javax.persistence.*

@Entity
class OneToManyUnidirectionalLeftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @OneToMany(cascade = [CascadeType.ALL])
    var rightList: List<OneToManyUnidirectionalRightEntity>? = null
}