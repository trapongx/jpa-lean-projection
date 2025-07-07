package com.runninglane.jpa.projection.test.cases.association.onetomany.collection

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class OneToManyUnidirectionalRightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var int: Int? = null
    var string: String? = null
}