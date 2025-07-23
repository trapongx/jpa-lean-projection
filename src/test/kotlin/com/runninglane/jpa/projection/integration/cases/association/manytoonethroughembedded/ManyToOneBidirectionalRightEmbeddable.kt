package com.runninglane.jpa.projection.integration.cases.association.manytoonethroughembedded

import javax.persistence.Embeddable
import javax.persistence.ManyToOne

@Embeddable
class ManyToOneBidirectionalRightEmbeddable {
    @ManyToOne
    var entity: ManyToOneBidirectionalRightEntity? = null
}