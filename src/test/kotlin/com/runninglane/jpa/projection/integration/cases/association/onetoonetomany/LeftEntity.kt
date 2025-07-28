package com.runninglane.jpa.projection.integration.cases.association.onetoonetomany

import javax.persistence.*

@Entity
class LeftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
    @OneToOne(cascade = [CascadeType.ALL])
    var middle: MiddleEntity? = null
}