package com.runninglane.jpa.projection.test.cases.id

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
data class EmbeddableId(
    var id1: Long? = null,
    var id2: Long? = null
) : Serializable