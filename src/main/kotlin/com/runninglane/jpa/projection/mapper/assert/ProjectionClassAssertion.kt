package com.runninglane.jpa.projection.mapper.assert

import com.runninglane.dto.buddy.annotation.DtoBuddyGenerated
import com.runninglane.jpa.projection.reflection.annotatedWith
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

object ProjectionClassAssertion {
    fun isCorrectSemantics(entityClass: KClass<*>, projectionClass: KClass<*>, projectionClassImpl: KClass<*>): Boolean {
        return when {
            // General projection requirement
            entityClass != projectionClass -> {
                !projectionClassImpl.isAbstract
                    && when (projectionClass != projectionClassImpl) {
                        // When the projection class is abstract
                        true -> projectionClassImpl.isSubclassOf(projectionClass)

                        // When the projection class is concrete and need to runtime code generation
                        false -> !projectionClass.annotatedWith<DtoBuddyGenerated>()
                    }
            }

            // When the parent projection class specifies property of the same type as in entity and does not want projection
            else -> {
                projectionClassImpl == projectionClass
            }
        }
    }
}