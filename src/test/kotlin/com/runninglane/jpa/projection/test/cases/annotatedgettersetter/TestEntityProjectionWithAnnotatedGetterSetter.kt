package com.runninglane.jpa.projection.test.cases.annotatedgettersetter

interface TestEntityProjectionWithAnnotatedGetterSetter {
    val id: Long

    var previousEntryId: Long?

    var isFirstEntry: Boolean
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getIsFirstEntry")
        get
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("setIsFirstEntry")
        set
}