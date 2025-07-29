package com.runninglane.jpa.projection.integration.cases.twowaytree

interface NodeProjection {
    val id: Long
    val name: String
    val parent: NodeProjection?
    val children: List<NodeProjection>?
}