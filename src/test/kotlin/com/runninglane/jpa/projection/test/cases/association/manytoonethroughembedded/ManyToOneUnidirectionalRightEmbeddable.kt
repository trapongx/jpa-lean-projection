package com.runninglane.jpa.projection.test.cases.association.manytoonethroughembedded

import javax.persistence.Embeddable
import javax.persistence.ManyToOne

@Embeddable
class ManyToOneUnidirectionalRightEmbeddable {
    @ManyToOne
    var entity: ManyToOneUnidirectionalRightEntity? = null
}