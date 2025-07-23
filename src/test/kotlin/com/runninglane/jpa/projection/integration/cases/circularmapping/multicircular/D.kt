package com.runninglane.jpa.projection.integration.cases.circularmapping.multicircular

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class D {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany
    var aList: MutableList<A> = mutableListOf()

    @OneToMany
    var bSet: MutableSet<B>? = null

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "d")
    lateinit var cList: MutableList<C>
}