package com.runninglane.jpa.projection.test.cases.association.manytoonethroughembedded
interface ManyToOneUnidirectionalLeftEntityProjection {
    val id: Long
    val right: ManyToOneUnidirectionalRightEmbeddableProjection?
}