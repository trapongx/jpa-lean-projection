package com.runninglane.jpa.projection.test.cases.annotatedgettersetter

import javax.persistence.*

@Entity
class TestEntity {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    var id: Long? = null

    var previousEntryId: Long? = null

    var isFirstEntry: Boolean
        @Access(AccessType.PROPERTY)
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getIsFirstEntry")
        get() = previousEntryId == null
        @Suppress("INAPPLICABLE_JVM_NAME", "UNUSED_PARAMETER")
        @JvmName("setIsFirstEntry")
        set(_) {}

}