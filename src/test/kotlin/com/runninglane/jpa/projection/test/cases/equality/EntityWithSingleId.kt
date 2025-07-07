package com.runninglane.jpa.projection.test.cases.equality

import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
class EntityWithSingleId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
}