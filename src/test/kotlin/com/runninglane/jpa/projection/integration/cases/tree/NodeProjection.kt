package com.runninglane.jpa.projection.integration.cases.tree

interface NodeProjection {
    val id: Long
    val name: String
    val parent: NodeProjection?
}