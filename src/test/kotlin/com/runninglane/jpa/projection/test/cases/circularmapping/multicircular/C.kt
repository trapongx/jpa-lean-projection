package com.runninglane.jpa.projection.test.cases.circularmapping.multicircular

import javax.persistence.*

@Entity
class C {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    lateinit var lateinit1: String
    var null1: String? = null
    var null2: String? = null
    var noneNull: Int = 0

    @ManyToOne(optional = false)
    var d: D? = null
}