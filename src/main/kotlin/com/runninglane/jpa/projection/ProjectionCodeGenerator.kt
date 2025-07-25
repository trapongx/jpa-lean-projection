package com.runninglane.jpa.projection

import com.runninglane.dto.buddy.bytecode.PropertyDescriptor
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeGenerator
import com.runninglane.jpa.projection.annotation.isAnnotatedForNoProjection
import com.runninglane.jpa.projection.reflection.annotatedWith
import com.squareup.kotlinpoet.*
import javax.persistence.EmbeddedId
import javax.persistence.Id
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

open class ProjectionCodeGenerator(
    val embeddableWithoutExplicitIdsEqualityStrategy: NoIdsStrategy =
        NoIdsStrategy.DONT_OVERRIDE,
    val noProjectionAnnotations: Set<KClass<out Annotation>> = emptySet()
) : KotlinCodeGenerator() {

    enum class NoIdsStrategy {
        DONT_OVERRIDE,
        USE_ALL_PROPERTIES_AS_IDS;
    }

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
            .takeIf { it.isNotEmpty() }
            ?: if (embeddableWithoutExplicitIdsEqualityStrategy == NoIdsStrategy.USE_ALL_PROPERTIES_AS_IDS) {
                dataCollector.entityClass.memberProperties.filterNot { it.isAnnotatedForNoProjection(noProjectionAnnotations) }
            } else { emptyList() }
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
            val idPropertyNames = idProperties.map { it.name }.sorted()

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
                    .addStatement(idPropertyNames.joinToString(" +\n    ") {
                        val isNullable = it in baseClassPropertiesNames && !idProperties.first { prop -> prop.name == it }.returnType.isMarkedNullable
                        if (isNullable) "(this.$it?.hashCode() ?: 0)" else "this.$it.hashCode()"
                    }.let { "return $it" })
                    .build()
            )

            val toStringFuncReturnStatementParams = mutableListOf<String>()
            val toStringFuncReturnStatement = buildString {
                appendLine("""return %S + "(" +""")
                toStringFuncReturnStatementParams.add(className)
                idPropertyNames.forEachIndexed { i, name ->
                    if (i > 0) {
                        append("%S + ")
                        toStringFuncReturnStatementParams.add(", ")
                    }
                    appendLine("""%S + "=" + %L +""")
                    toStringFuncReturnStatementParams.add(name)
                    toStringFuncReturnStatementParams.add(name)
                }
                appendLine("""")"""")
            }
            updatedBuilder = updatedBuilder.addFunction(
                FunSpec.builder("toString")
                    .addModifiers(KModifier.OVERRIDE)
                    .returns(String::class)
                    .addStatement(toStringFuncReturnStatement, *toStringFuncReturnStatementParams.toTypedArray())
                    .build()
            )
        }

        return updatedBuilder
    }
}