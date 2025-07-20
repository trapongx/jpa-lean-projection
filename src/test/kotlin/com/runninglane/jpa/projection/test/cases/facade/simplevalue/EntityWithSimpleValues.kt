package com.runninglane.jpa.projection.test.cases.facade.simplevalue

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
open class EntityWithSimpleValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
    open var boolean: Boolean? = null
    open var byte: Byte? = null
    open var short: Short? = null
    open var char: Char? = null
    open var int: Int = 0
    open var long: Long? = null
    open var float: Float? = null
    open var double: Double? = null
    open var string: String? = null
    open var date: Date? = null
    open var localDate: LocalDate? = null
    open var bigInteger: BigInteger? = null
    open var bigDecimal: BigDecimal? = null
    @Lob
    open var blob: ByteArray? = null
    @Lob
    open var clob: String? = null
    @Enumerated(EnumType.STRING)
    open var enum: TestEnum? = null
    open var uuid: UUID? = null
    open var duration: Duration? = null
    open var instant: Instant? = null
}