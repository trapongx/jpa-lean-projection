package com.runninglane.jpa.projection.mapper

import com.runninglane.jpa.projection.string.capitalizeFirst
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.*
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

internal interface PropertyAccessor {
    fun get(obj: Any): Any?
    fun set(obj: Any, value: Any?)

    companion object {
        private val cache: MutableMap<Pair<KClass<*>, String>, PropertyAccessor> = mutableMapOf()

        fun of(
            entityClass: KClass<*>,
            projectionClassImpl: KClass<*>,
            propertyName: String
        ): PropertyAccessor {
            return cache.getOrPut(projectionClassImpl to propertyName) {
                val propertyGetter: KProperty1.Getter<*, *>? = projectionClassImpl.memberProperties
                    .filter { it.visibility == KVisibility.PUBLIC }
                    .firstOrNull { it.name == propertyName }
                    ?.getter

                val getterFunction: KFunction<*>? by lazy {
                    val getterNames = listOf("get", "is").map { "$it${propertyName.capitalizeFirst()}" }
                    projectionClassImpl.functions
                        .filter { it.visibility == KVisibility.PUBLIC }
                        .firstOrNull { it.name in getterNames }
                }

                val returnType by lazy { propertyGetter?.returnType ?: getterFunction?.returnType }

                val getter: (Any) -> Any? = propertyGetter?.let { { target -> it.call(target) } }
                    ?: getterFunction?.let { { target -> it.call(target) } }
                    ?: error("Accessible getter for property `$propertyName` not found in class `${projectionClassImpl.qualifiedName}`")

                val propertySetter: KMutableProperty1.Setter<*, *>? = projectionClassImpl.memberProperties
                    .filterIsInstance<KMutableProperty1<*, *>>()
                    .filter { it.visibility == KVisibility.PUBLIC }
                    .firstOrNull { it.name == propertyName }
                    ?.setter

                val setterFunction: KFunction<*>? by lazy {
                    val setterName = "set${propertyName.capitalizeFirst()}"
                    projectionClassImpl.functions
                        .filter { it.visibility == KVisibility.PUBLIC }
                        .firstOrNull { it.name == setterName && it.parameters.size == 2 && it.parameters[1].type == returnType }
                }

                fun catchInvocationTargetException(block: () -> Unit) {
                    try {
                        block()
                    } catch (e: InvocationTargetException) {
                        throw e.targetException
                    }
                }
                val setter: (Any, Any?) -> Unit = propertySetter?.let {
                    { target, value -> catchInvocationTargetException { it.call(target, value) } }
                } ?: setterFunction?.let {
                    { target, value -> catchInvocationTargetException { it.call(target, value) } }
                } ?: error("Accessible setter for property `$propertyName` not found in class `${projectionClassImpl.qualifiedName}`")


                PropertyAccessorImpl(getter, setter)
            }
        }
    }
}

private class PropertyAccessorImpl(val getter: (Any) -> Any?, val setter: (Any, Any?) -> Unit) : PropertyAccessor {
    override fun get(obj: Any): Any? = getter(obj)

    override fun set(obj: Any, value: Any?) {
        setter(obj, value)
    }
}
