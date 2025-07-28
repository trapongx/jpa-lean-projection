package com.runninglane.jpa.projection.integration.cases.postprocess

import javax.persistence.*

@Entity
class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null

    @Transient
    var nameCapitalized: String? = null

    @PostLoad
    fun postLoad() {
        nameCapitalized = name?.uppercase()
    }
}