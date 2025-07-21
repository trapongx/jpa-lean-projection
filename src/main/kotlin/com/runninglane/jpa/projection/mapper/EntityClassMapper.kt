package com.runninglane.jpa.projection.mapper

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.annotations.NoProjection
import com.runninglane.jpa.projection.mapper.association.AnyToManyPropertyMapper
import com.runninglane.jpa.projection.mapper.association.AnyToOnePropertyMapper
import com.runninglane.jpa.projection.mapper.elementcollection.ElementCollectionPropertyMapper
import com.runninglane.jpa.projection.mapper.embedded.EmbeddedPropertyMapper
import com.runninglane.jpa.projection.mapper.map.MapPropertyMapper
import com.runninglane.jpa.projection.mapper.sametype.SameTypePropertyMapper
import com.runninglane.jpa.projection.reflection.annotatedWith
import javax.persistence.*
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

internal class EntityClassMapper(
    private val projectorFactory: ProjectorFactory,
    private val parent: Mapper?,
    val entityClass: KClass<*>,
    val projectionClass: KClass<*>,
    override val projectionClassImpl: KClass<*>,
    hadJoinFetch: Boolean,
    projectedPropertyNames: Set<String>? // Not null when used by fetcher
) : BaseEntityClassMapper {

    init {
        assert(
            projectionClass != projectionClassImpl
                    && !projectionClassImpl.isAbstract
                    && projectionClassImpl.isSubclassOf(projectionClass)
        )
    }

    private var hasJoinFetch: Boolean = false

    /**
     * The order of traversing projected properties matters because we have to know first whether the projection includes
     * all ID properties or not. If not, plural association cannot be done neither by joined fetch nor separate query.
     */
    private val projectedProperties: List<KProperty1<out Any, *>> = projectionClassImpl.memberProperties
        .filter { it.visibility == KVisibility.PUBLIC }
        .filterNot {
            it.annotatedWith<NoProjection>()
                    || projectionClass.memberProperties.find { prop -> prop.name == it.name }?.annotatedWith<NoProjection>() == true
        }
        .let { allProps ->
            if (projectedPropertyNames != null) {
                allProps.filter { projectedPropertyNames.contains(it.name) }
            } else {
                allProps
            }
        }
        .sortedBy { prop ->
            val srcProp = entityClass.memberProperties.find { it.name == prop.name }
                ?: error("Projected property `${prop.name}` not found in class `${entityClass.qualifiedName}`")
            when {
                srcProp.annotatedWith<Id>() || srcProp.annotatedWith<EmbeddedId>() -> 0
                srcProp.annotatedWith<Embedded>() -> 1
                srcProp.annotatedWith<OneToMany>() || srcProp.annotatedWith<ManyToMany>() -> 2
                srcProp.annotatedWith<ElementCollection>() -> 3
                srcProp.annotatedWith<OneToOne>() || srcProp.annotatedWith<ManyToOne>() -> 4
                else -> 5
            }
        }

    private val mappers: List<Mapper> = run {
        val idPropNamesNotMappedWithSameType: MutableSet<String> = entityClass.memberProperties
            .filter { it.annotatedWith<Id>() || it.annotatedWith<EmbeddedId>() }
            .map { it.name }
            .sorted()
            .also {
                require(it.isNotEmpty()) {
                    "Not ID properties found in entity class `${entityClass.qualifiedName}`"
                }
            }
            .toMutableSet()

        fun errorIfIdPropNamesNotExhausted(propNameInTopic: String) {
            if (idPropNamesNotMappedWithSameType.isNotEmpty()) {
                error("Projection from `${entityClass.qualifiedName}.${propNameInTopic}` to `${projectionClassImpl.qualifiedName}.${propNameInTopic}` is not possible when not all ID properties mapped with exact same type as source")
            }
        }

        projectedProperties.map { prop ->
            val propName = prop.name
            val propType = prop.returnType.jvmErasure
            val entityProp = entityClass.memberProperties
                .find { it.name == prop.name }
                ?: error("Projected property `$propName` not found in class `${entityClass.qualifiedName}`")
            val entityPropType = entityProp.returnType.jvmErasure
            val isMap = entityPropType.isSubclassOf(Map::class)

            val mapper = if (isMap && (entityProp.annotatedWith<OneToMany>() || entityProp.annotatedWith<ManyToMany>() || entityProp.annotatedWith<ElementCollection>())) {
                val propertyInfo = PropertyInfo(
                    prop,
                    prop.name,
                    propType,
                    PropertyAccessor.of(projectorFactory.projectionFactory, entityClass, projectionClassImpl, propName),
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
                MapPropertyMapper(
                    projectorFactory,
                    this,
                    entityClass,
                    projectionClass,
                    projectionClassImpl,
                    propertyInfo,
                    hadJoinFetch || hasJoinFetch
                ).simplify()
            } else if (entityProp.annotatedWith<OneToMany>() || entityProp.annotatedWith<ManyToMany>()) {
                errorIfIdPropNamesNotExhausted(propName)
                AnyToManyPropertyMapper(
                    projectorFactory,
                    this,
                    entityClass,
                    projectionClass,
                    projectionClassImpl,
                    propName,
                    hadJoinFetch || hasJoinFetch
                ).simplify()
            } else if (entityProp.annotatedWith<OneToOne>() || entityProp.annotatedWith<ManyToOne>()) {
                AnyToOnePropertyMapper(
                    projectorFactory,
                    this,
                    entityClass,
                    projectionClass,
                    projectionClassImpl,
                    propName,
                    hadJoinFetch || hasJoinFetch
                ).simplify()
            } else if (entityProp.annotatedWith<ElementCollection>()) {
                errorIfIdPropNamesNotExhausted(propName)
                ElementCollectionPropertyMapper(
                    projectorFactory,
                    this,
                    entityClass,
                    projectionClass,
                    projectionClassImpl,
                    propName,
                    hadJoinFetch || hasJoinFetch
                ).simplify()
            } else if (propType == entityPropType) {
                SameTypePropertyMapper(projectorFactory, this, entityClass, projectionClassImpl, propName).also {
                    idPropNamesNotMappedWithSameType.remove(it.propertyName)
                }
            } else if (entityProp.annotatedWith<Embedded>() || entityProp.annotatedWith<EmbeddedId>()) {
                EmbeddedPropertyMapper(
                    projectorFactory,
                    this,
                    entityClass,
                    projectionClass,
                    projectionClassImpl,
                    prop.name,
                    hadJoinFetch || hasJoinFetch
                )
            } else {
                error("Unsupported projection of property `$propName` in class ${projectionClass.qualifiedName} from `${entityClass.qualifiedName}` to `${projectionClass.qualifiedName}`")
            }

            if (!hadJoinFetch && !hasJoinFetch && mapper.hasJoinFetch()) hasJoinFetch = true

            mapper
        }.let { mappers ->
            if (hasJoinFetch) mappers.sortedBy { it.hasJoinFetch() } else mappers
        }
    }

    private val idMappers: List<SameTypePropertyMapper> =
        mappers.filterIsInstance<SameTypePropertyMapper>().filter { it.isIdProperty }

    override fun getParent(): Mapper? = parent

    override fun getChildren(): List<Mapper> = mappers

    override fun hasJoinFetch(): Boolean = hasJoinFetch

    override fun buildSelections(
        path: Path<*>,
        tupleIndexCounter: TupleIndexCounter
    ): List<Expression<*>> = mappers.flatMap { it.buildSelections(path, tupleIndexCounter) }

    override fun readId(tuple: Tuple): Any? = when (idMappers.size) {
        1 -> tuple[idMappers.first().tupleIndex]
        else -> idMappers.associate { it.propertyName to tuple[it.tupleIndex] }
    }

    override fun isIdNull(tuple: Tuple): Boolean = idMappers.all { tuple[it.tupleIndex] == null }

    override fun readTuple(
        tuple: Tuple,
        projection: Any,
        parentProjection: Any?,
        projectionIdentityMap: ProjectionIdentityMap
    ): Pair<List<Fetcher>, HydrationMaterial?> =
        mappers.map { it.readTuple(tuple, projection, parentProjection, projectionIdentityMap) }.flatten()

    companion object {
        data class CacheKey(
            val entityClass: KClass<*>,
            val projectionClass: KClass<*>,
            val projectionClassImpl: KClass<*>,
            val hadJoinFetch: Boolean,
            val projectedPropertyNames: Set<String>?
        ) {
            init {
                assert(
                    projectionClass != projectionClassImpl
                            && !projectionClassImpl.isAbstract
                            && projectionClassImpl.isSubclassOf(projectionClass)
                )
            }
        }

        private val cache: MutableMap<CacheKey, EntityClassMapper> = mutableMapOf()

        fun of(
            projectorFactory: ProjectorFactory,
            parent: Mapper?,
            entityClass: KClass<*>,
            projectionClass: KClass<*>,
            hadJoinFetch: Boolean,
            projectedPropertyNames: Set<String>?,
        ): BaseEntityClassMapper {
            val projectionClassImpl = projectorFactory.projectionFactory.getImplementation(entityClass, projectionClass)

            val isCircular = generateSequence(parent) { it.getParent() }
                .filterIsInstance<EntityClassMapper>()
                .any { it.entityClass == entityClass }

            if (isCircular) {
                return EntityClassMapperUsingFetcher(projectorFactory, parent, entityClass, projectionClass, projectionClassImpl)
            }

            val newInstance by lazy {
                EntityClassMapper(
                    projectorFactory,
                    parent,
                    entityClass, projectionClass, projectionClassImpl, hadJoinFetch, projectedPropertyNames
                )
            }

            return when (parent) {
                null -> {
                    val key = CacheKey(entityClass, projectionClass, projectionClassImpl, hadJoinFetch, projectedPropertyNames)
                    cache[key] ?: newInstance.also { cache[key] = it }
                }

                else -> newInstance
            }
        }

        fun of(
            projectorFactory: ProjectorFactory,
            entityClass: KClass<*>,
            projectionClass: KClass<*>,
            hadJoinFetch: Boolean,
            projectedPropertyNames: Set<String>?,
        ): EntityClassMapper = of(
            projectorFactory,
            null,
            entityClass, projectionClass, hadJoinFetch, projectedPropertyNames
        ) as EntityClassMapper
    }
}