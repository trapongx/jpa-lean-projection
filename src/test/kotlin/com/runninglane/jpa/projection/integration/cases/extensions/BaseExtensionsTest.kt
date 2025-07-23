package com.runninglane.jpa.projection.integration.cases.extensions

import com.runninglane.jpa.projection.integration.BaseTest

abstract class BaseExtensionsTest : BaseTest() {
    protected fun createDefaultTestEntities() {
        for (i in 1..10) {
            VerySimpleEntity().apply {
                int = i
                string = "Test$i"
            }.also { entityManager.persist(it) }
        }
    }
}