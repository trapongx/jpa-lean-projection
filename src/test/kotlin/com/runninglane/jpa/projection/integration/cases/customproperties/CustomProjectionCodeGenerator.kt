package com.runninglane.jpa.projection.integration.cases.customproperties

import com.runninglane.dto.buddy.bytecode.PropertyDescriptor
import com.runninglane.jpa.projection.ProjectionCodeGenerator
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class CustomProjectionCodeGenerator : ProjectionCodeGenerator() {
    override fun implementProperties(
        builder: TypeSpec.Builder,
        properties: List<PropertyDescriptor>,
        typeParamsMapByName: Map<String, KClass<*>>?,
        dataCollector: Any?
    ): TypeSpec.Builder {
        val properties = properties.toMutableList()
        val projectionClass = (dataCollector as DataCollector).baseClass

        val updatedBuilder = when {
            projectionClass.isSubclassOf(CustomProjectionSuperInterface1::class) -> {
                properties.removeIf { it.name == "delegate" }

                val entityClass = projectionClass.supertypes.find {
                    it.toString().contains(CustomProjectionSuperInterface1::class.simpleName!!)
                }?.arguments?.first()?.type!!.classifier as KClass<*>

                builder.addProperty(
                    com.squareup.kotlinpoet.PropertySpec.builder(
                        "delegate",
                        entityClass.asTypeName(),
                        KModifier.OVERRIDE
                    ).delegate("lazy { TODO(\"id = \$id\") }").build()
                )
            }

            projectionClass.isSubclassOf(CustomProjectionSuperInterface2::class) -> {
                properties.removeIf { it.name == "customString" }

                builder.addProperty(
                    com.squareup.kotlinpoet.PropertySpec.builder(
                        "customString",
                        String::class.asTypeName(),
                        KModifier.OVERRIDE
                    )
                        .mutable(true)
                        .initializer("\"Hello\"")
                        .build()
                )
            }

            else -> builder
        }

        return super.implementProperties(updatedBuilder, properties.toList(), typeParamsMapByName, dataCollector)
    }

}