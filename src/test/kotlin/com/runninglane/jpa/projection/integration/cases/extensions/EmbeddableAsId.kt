package com.runninglane.jpa.projection.integration.cases.extensions

import java.io.Serializable

class EmbeddableAsId: Serializable {
    var id: Long? = null
    var int: Int? = null
    var string: String? = null
}