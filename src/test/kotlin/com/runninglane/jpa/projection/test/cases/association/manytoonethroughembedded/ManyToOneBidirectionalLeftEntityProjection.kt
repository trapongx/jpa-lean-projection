package com.runninglane.jpa.projection.test.cases.association.manytoonethroughembedded

interface ManyToOneBidirectionalLeftEntityProjection {
    val id: Long
    val right: ManyToOneBidirectionalRightEmbeddableProjection?
}