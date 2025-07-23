package com.runninglane.jpa.projection.integration.cases.association.onetoone

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class OneToOneUnidirectionalRightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var int: Int? = null
    var string: String? = null
}