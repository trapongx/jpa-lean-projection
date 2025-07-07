package com.runninglane.jpa.projection

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.ThreeStepsByteCodeStrategy
import kotlin.reflect.KClass

class ProjectionFactory(
    val dtoBuddy: DtoBuddy =
        DtoBuddy(ThreeStepsByteCodeStrategy(ProjectionCodeGenContributor()))
) {
    // This is Map<Pair<projectionClass, entityClass>, implementationClass>
    private val implementationMap = mutableMapOf<Pair<KClass<*>, KClass<*>>, KClass<*>>()

    fun getImplementation(entityClass: KClass<*>, projectionClass: KClass<*>): KClass<*> {
        val key = projectionClass to entityClass
        return implementationMap.getOrPut(key) {
            dtoBuddy.implement(
                projectionClass,
                dataCollector = ProjectionCodeGenContributor.DataCollector(entityClass)
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> create(entityClass: KClass<*>, projectionClass: KClass<T>): T {
        return dtoBuddy.create(getImplementation(entityClass, projectionClass))
    }
}