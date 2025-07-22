package com.runninglane.jpa.projection.mapper.embedded

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.annotations.isAnnotatedForNoProjection
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.mapper.assert.ProjectionClassAssertion
import com.runninglane.jpa.projection.mapper.association.AnyToManyPropertyMapper
import com.runninglane.jpa.projection.mapper.association.AnyToOnePropertyMapper
import com.runninglane.jpa.projection.mapper.elementcollection.ElementCollectionPropertyMapper
import com.runninglane.jpa.projection.mapper.map.MapPropertyMapper
import com.runninglane.jpa.projection.mapper.sametype.SameTypePropertyMapper
import com.runninglane.jpa.projection.reflection.annotatedWith
import javax.persistence.*
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

internal class EmbeddableClassMapper(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper?,
    private val embeddableClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    private val hadJoinFetch: Boolean
) : Mapper {

    init {
        assert(ProjectionClassAssertion.isCorrectSemantics(embeddableClass, projectionClass, projectionClassImpl))
    }

    private var hasJoinFetch: Boolean = false

    private val mappers: List<Mapper> = run {
        projectionClassImpl.memberProperties
            .filter { it.visibility == KVisibility.PUBLIC }
            .filterNot {
                it.isAnnotatedForNoProjection(projectorFactory)
                        || projectionClass.memberProperties.find { prop -> prop.name == it.name }?.isAnnotatedForNoProjection(projectorFactory) == true
            }
            .map { prop ->
                val propName = prop.name
                val propType = prop.returnType.jvmErasure
                val entityProp = embeddableClass.memberProperties
                    .find { it.name == prop.name }
                    ?: error("Projected property `$propName` not found in class `${embeddableClass.qualifiedName}`")
                val entityPropType = entityProp.returnType.jvmErasure
                val isMap = entityPropType.isSubclassOf(Map::class)

                val mapper = if (entityProp.annotatedWith<Embedded>()) {
                    EmbeddedPropertyMapper(
                        projectorFactory,
                        this,
                        embeddableClass,
                        projectionClass,
                        projectionClassImpl,
                        prop.name,
                        hadJoinFetch || hasJoinFetch
                    )
                } else if (isMap && (entityProp.annotatedWith<OneToMany>() || entityProp.annotatedWith<ElementCollection>())) {
                    val propertyInfo = PropertyInfo(
                        prop,
                        prop.name,
                        propType,
                        PropertyAccessor.of(
                            projectorFactory.projectionFactory,
                            embeddableClass, projectionClassImpl, propName
                        ),
                        isCollection = false,
                        isList = false,
                        isSet = false,
                        isMap = true,
                        entityPropType.isSubclassOf(MutableMap::class),
                        prop.returnType.arguments.first().type!!.jvmErasure,
                        prop.returnType.arguments.last().type!!.jvmErasure,
                        entityProp.returnType.arguments.first().type!!.jvmErasure,
                        entityProp.returnType.arguments.last().type!!.jvmErasure,
                    )
                    MapPropertyMapper(projectorFactory, this, embeddableClass, projectionClass, projectionClassImpl, propertyInfo, hadJoinFetch || hasJoinFetch).simplify()
                } else if (entityProp.annotatedWith<OneToMany>()) {
                    AnyToManyPropertyMapper(
                        projectorFactory,
                        this,
                        embeddableClass,
                        projectionClass,
                        projectionClassImpl,
                        propName,
                        hadJoinFetch || hasJoinFetch
                    ).simplify()
                } else if (entityProp.annotatedWith<OneToOne>() || entityProp.annotatedWith<ManyToOne>()) {
                    AnyToOnePropertyMapper(
                        projectorFactory,
                        this,
                        embeddableClass,
                        projectionClass,
                        projectionClassImpl,
                        propName,
                        hadJoinFetch || hasJoinFetch
                    ).simplify()
                } else if (entityProp.annotatedWith<ElementCollection>()) {
                    ElementCollectionPropertyMapper(
                        projectorFactory,
                        this,
                        embeddableClass,
                        projectionClass,
                        projectionClassImpl,
                        propName,
                        hadJoinFetch || hasJoinFetch
                    ).simplify()
                } else if (propType == entityPropType) {
                    SameTypePropertyMapper(projectorFactory, this, embeddableClass, projectionClassImpl, propName)
                } else {
                    error("Unsupported projection of property `$propName` in class ${projectionClass.qualifiedName} from `${embeddableClass.qualifiedName}` to `${projectionClass.qualifiedName}`")
                }

            if (!hadJoinFetch && !hasJoinFetch && mapper.hasJoinFetch()) hasJoinFetch = true

            mapper
        }.let { mappers ->
            if (hasJoinFetch) mappers.sortedBy { it.hasJoinFetch() } else mappers
        }
    }

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = mappers

    override fun hasJoinFetch(): Boolean = hasJoinFetch

    override fun buildSelections(path: Path<*>, tupleIndexCounter: TupleIndexCounter): List<Expression<*>> {
        return mappers.flatMap { it.buildSelections(path, tupleIndexCounter) }
    }

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> {
        val fetchers = mappers.flatMap { mapper ->
            mapper.readTuple(tuple, projection, parentProjection, projectionIdentityMap).first
        }
        return fetchers to HydrationMaterial(projection, parentProjection)
    }
}