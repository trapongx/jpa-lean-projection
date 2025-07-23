package com.runninglane.jpa.projection.integration.cases.association.manytoonethroughembedded

interface ManyToOneBidirectionalLeftEntityProjection {
    val id: Long
    val right: ManyToOneBidirectionalRightEmbeddableProjection?
}