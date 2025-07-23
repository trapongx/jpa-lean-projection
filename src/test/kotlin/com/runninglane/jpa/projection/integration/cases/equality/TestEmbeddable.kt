package com.runninglane.jpa.projection.integration.cases.equality

import javax.persistence.Embeddable

@Embeddable
class TestEmbeddable {
    var name: String? = null
    var score: Int? = null
}