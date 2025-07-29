package com.runninglane.jpa.projection.integration.cases.treeusingmap

interface NodeProjection {
    val id: Long
    val name: String
    val children: MutableMap<NodeProjection, String>?
}