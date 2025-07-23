package com.runninglane.jpa.projection.integration.cases.association.manytoonethroughembedded
interface ManyToOneUnidirectionalLeftEntityProjection {
    val id: Long
    val right: ManyToOneUnidirectionalRightEmbeddableProjection?
}