package com.runninglane.jpa.projection.test.cases.circularmapping.selfassociate

import javax.persistence.*

@Entity
class A {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne
    var parent: A? = null
}