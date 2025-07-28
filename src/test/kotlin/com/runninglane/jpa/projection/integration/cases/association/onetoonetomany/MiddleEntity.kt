package com.runninglane.jpa.projection.integration.cases.association.onetoonetomany

import javax.persistence.*

@Entity
class MiddleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
    @OneToMany
    var rightList: List<RightEntity>? = null
}