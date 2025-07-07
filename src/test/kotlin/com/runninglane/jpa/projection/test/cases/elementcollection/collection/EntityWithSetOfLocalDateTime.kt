package com.runninglane.jpa.projection.test.cases.elementcollection.collection

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class EntityWithSetOfLocalDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ElementCollection
    var elements: Set<LocalDateTime>? = null
}