package com.runninglane.jpa.projection.integration.cases.twowaytree

import javax.persistence.*

@Entity
class NodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
    @ManyToOne
    var parent: NodeEntity? = null
    @OneToMany(mappedBy = "parent")
    var children: MutableList<NodeEntity>? = null
}