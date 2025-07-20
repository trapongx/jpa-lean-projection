package com.runninglane.jpa.projection

import com.runninglane.dto.buddy.bytecode.PropertyDescriptor
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeGenerator
import com.runninglane.jpa.projection.reflection.annotatedWith
import com.squareup.kotlinpoet.*
import javax.persistence.EmbeddedId
import javax.persistence.Id
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

open class ProjectionCodeGenerator : KotlinCodeGenerator() {

    class DataCollector(val entityClass: KClass<*>, val baseClass: KClass<*>) {
        lateinit var className: String
        val attributes: MutableMap<String, Any?> = mutableMapOf()
    }

    override fun defineClass(
        baseClass: KClass<*>,
        typeParams: List<KClass<*>>?,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): TypeSpec.Builder {
        dataCollector as DataCollector
        dataCollector.className = className

        return super.defineClass(baseClass, typeParams, packageName, className, dataCollector)
    }

    override fun implementProperties(
        builder: TypeSpec.Builder,
        properties: List<PropertyDescriptor>,
        typeParamsMapByName: Map<String, KClass<*>>?,
        dataCollector: Any?
    ): TypeSpec.Builder {
        var updatedBuilder = super.implementProperties(builder, properties, typeParamsMapByName, dataCollector)

        dataCollector as DataCollector
        val baseClassPropertiesNames = dataCollector.baseClass.memberProperties.map { it.name }
        val idProperties = dataCollector.entityClass.memberProperties
            .filter { it.annotatedWith<Id>() || it.annotatedWith<EmbeddedId>() }
        val missingIdProperties = idProperties.filter { !baseClassPropertiesNames.contains(it.name) }
        missingIdProperties.forEach { property ->
            val typeName = property.returnType.asTypeName()
            val defaultValue = getDefaultValueForType(typeName)
            val propertySpec = PropertySpec.builder(property.name, typeName)
                .mutable(true)
                .let {
                    if (defaultValue == null && !property.returnType.isMarkedNullable)
                        it.addModifiers(KModifier.LATEINIT)
                    else
                        it.initializer(defaultValue ?: "null")
                }

            // Add the property to the class
            updatedBuilder = updatedBuilder.addProperty(propertySpec.build())
        }

        if (idProperties.isNotEmpty()) {
            val idPropertyNames = idProperties.map { it.name }

            val className = dataCollector.className
            val escapedClassName = if (className.contains("$")) "`$className`" else className

            updatedBuilder = updatedBuilder.addFunction(
                FunSpec.builder("equals")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("other", Any::class.asClassName().copy(nullable = true))
                    .returns(Boolean::class)
                    .addStatement("if (this === other) return true")
                    .addStatement("if (other !is $escapedClassName)")
                    .addStatement("\treturn false")
                    .addStatement(idPropertyNames.joinToString("\n&& ") { "this.$it == other.$it" }.let { "return $it" })
                    .build()
            )

            updatedBuilder = updatedBuilder.addFunction(
                FunSpec.builder("hashCode")
                    .addModifiers(KModifier.OVERRIDE)
                    .returns(Int::class)
                    .addStatement(idPropertyNames.joinToString(" +\n    ") { "(this.$it?.hashCode() ?: 0)" }
                        .let { "return $it" })
                    .build()
            )

            updatedBuilder = updatedBuilder.addFunction(
                FunSpec.builder("toString")
                    .addModifiers(KModifier.OVERRIDE)
                    .returns(String::class)
                    .addStatement("""return "${className.replace("$", "\\$")}(" + ${
                        idPropertyNames.joinToString(" + \", \" + \n    ") { "\"$it=\${this.$it}\"" }
                    } + ${"\n"}")" """)
                    .build()
            )
        }

        return updatedBuilder
    }
}