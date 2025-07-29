package com.runninglane.jpa.projection.integration.cases.treeusingmap

import javax.persistence.*

@Entity
class NodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
    @ElementCollection
    var children: MutableMap<NodeEntity, String>? = null
}