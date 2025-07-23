package com.runninglane.jpa.projection.integration.cases.simplevalue

import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.persistence.*

enum class TestEnum {
    ONE, TWO
}

@Entity
class EntityWithSimpleValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var boolean: Boolean? = null
    var byte: Byte? = null
    var short: Short? = null
    var char: Char? = null
    var int: Int = 0
    var long: Long? = null
    var float: Float? = null
    var double: Double? = null
    var string: String? = null
    var date: Date? = null
    var localDate: LocalDate? = null
    var bigInteger: BigInteger? = null
    var bigDecimal: BigDecimal? = null
    @Lob
    var blob: ByteArray? = null
    @Lob
    var clob: String? = null
    @Enumerated(EnumType.STRING)
    var enum: TestEnum? = null
    var uuid: UUID? = null
    var duration: Duration? = null
    var instant: Instant? = null
}