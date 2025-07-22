package com.runninglane.jpa.projection

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeGenBasedByteCodeStrategy
import kotlin.reflect.KClass

class ProjectionFactory(
    val dtoBuddy: DtoBuddy = DtoBuddy(
        KotlinCodeGenBasedByteCodeStrategy(
            ProjectionCodeGenerator()
        )
    ),
    val noProjectionAnnotations: Set<KClass<out Annotation>> = emptySet()
) {
    // This is Map<Pair<projectionClass, entityClass>, implementationClass>
    private val implementationMap = mutableMapOf<Pair<KClass<*>, KClass<*>>, KClass<*>>()

    fun getImplementation(entityClass: KClass<*>, projectionClass: KClass<*>): KClass<*> {
        if (projectionClass == entityClass) {
            throw IllegalArgumentException(buildString {
                append("Projection class cannot be same as entity class.")
                append(" projectionClass: $projectionClass, entityClass: $entityClass")
            })
        }
        val key = projectionClass to entityClass
        return implementationMap.getOrPut(key) {
            dtoBuddy.implement(
                projectionClass,
                dataCollector = ProjectionCodeGenerator.DataCollector(entityClass, projectionClass)
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> create(entityClass: KClass<*>, projectionClass: KClass<T>): T {
        return dtoBuddy.create(getImplementation(entityClass, projectionClass))
    }
}

inline fun <reified E, reified P> ProjectionFactory.getImplementation(): KClass<*> =
    getImplementation(E::class, P::class)

inline fun <reified E, reified P : Any> ProjectionFactory.create(): P =
    create(E::class, P::class)
