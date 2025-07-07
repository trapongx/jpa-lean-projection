package com.runninglane.jpa.projection.test.cases.customproperties

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class TestEntity {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
}