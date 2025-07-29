package com.runninglane.jpa.projection.integration.cases.tree

import javax.persistence.*

@Entity
class NodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
    @ManyToOne
    var parent: NodeEntity? = null
}