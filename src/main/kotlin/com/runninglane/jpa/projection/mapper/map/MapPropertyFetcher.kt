package com.runninglane.jpa.projection.mapper.map

import com.runninglane.jpa.projection.HydrationMaterial
import com.runninglane.jpa.projection.ProjectionIdentityMap
import com.runninglane.jpa.projection.ProjectorFactory
import com.runninglane.jpa.projection.mapper.*
import com.runninglane.jpa.projection.mapper.association.ProbablyInvertible
import com.runninglane.jpa.projection.mapper.sametype.SameTypePropertyMapper
import com.runninglane.jpa.projection.reflection.annotatedWith
import com.runninglane.jpa.projection.reflection.getAnnotation
import com.runninglane.jpa.projection.reflection.getEntityClass
import com.runninglane.jpa.projection.reflection.getPropertyAtPath
import javax.persistence.EntityManager
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Tuple
import javax.persistence.criteria.Expression
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Path
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

internal class MapPropertyFetcher(
    private val projectorFactory: ProjectorFactory,
    private val entityClass: KClass<*>,
    private val projectionClass: KClass<*>,
    private val projectionClassImpl: KClass<*>,
    private val propertyName: String,
    private val propertyInfo: PropertyInfo,
    private val projections: List<Any>
) : Fetcher {

    private val idProp: KProperty1<*, *> = getProjectionIdProp(projectionClass, entityClass)

    private val mapper = AsMapper()

    inner class AsMapper : Mapper, ProbablyInvertible {
        val idMapper = SameTypePropertyMapper(projectorFactory, this, entityClass, projectionClassImpl, idProp.name)

        val keyMapper = buildKeyOrValueMapper(true)

        val valueMapper = buildKeyOrValueMapper(false)

        private val mappers = listOf(idMapper, keyMapper, valueMapper)

        override fun getParent(): Mapper? = null

        override fun getChildren(): List<Mapper> = mappers

        override fun hasJoinFetch(): Boolean = true

        private fun buildKeyOrValueMapper(isForKey: Boolean): Mapper {
            val (srcType, destType) = when {
                isForKey -> propertyInfo.srcPropTypeArgType1!! to propertyInfo.propTypeArgType1!!
                else -> propertyInfo.srcPropTypeArgType2!! to propertyInfo.propTypeArgType2!!
            }
            return if (srcType == destType) {
                SameTypeMapKeyOrValueMapper(this, isForKey)
            } else {
                require(srcType.getEntityClass() != null) {
                    "Unsupported type `${srcType}` as projection of map ${if (isForKey) "key" else "value"}"
                }
                EntityClassMapper.of(
                    projectorFactory,
                    this,
                    srcType,
                    destType,
                    true,
                    null
                )
            }
        }

        override fun buildSelections(
            path: Path<*>,
            tupleIndexCounter: TupleIndexCounter
        ): List<Expression<*>> = mappers.flatMap { it.buildSelections(path, tupleIndexCounter) }

        override fun readTuple(
            tuple: Tuple,
            projection: Any,
            parentProjection: Any?,
            projectionIdentityMap: ProjectionIdentityMap
        ): Pair<List<Fetcher>, HydrationMaterial?> {
            return readTupleIntoMap(
                propertyInfo,
                projection,
                parentProjection
            ) {
                val fetchers = mutableListOf<Fetcher>()

                val entry = MapEntry()
                listOf(
                    keyMapper to entry::key,
                    valueMapper to entry::value
                ).forEach { (mapper, prop) ->
                    when (mapper) {
                        is SameTypeMapKeyOrValueMapper -> {
                            mapper.readTuple(tuple, entry, projection, projectionIdentityMap)
                        }

                        else -> {
                            mapper as EntityClassMapper
                            val id = mapper.readId(tuple)!!
                            val instance = projectionIdentityMap.get(mapper.entityClass, mapper.projectionClassImpl, id)
                                ?: projectorFactory.projectionFactory
                                    .create(mapper.entityClass, mapper.projectionClass)
                                    .also { projectionIdentityMap.add(mapper.entityClass, mapper.projectionClassImpl, id, it) }
                            prop.setter.call(instance)
                            mapper.readTuple(tuple, instance, projection, projectionIdentityMap)
                                .also { fetchers.addAll(it.first) }
                        }
                    }
                }

                (entry.key to entry.value) to fetchers.toList()
            }
        }

        override fun checkAssociationInvertibility(
            entityClassOnRightSide: KClass<*>,
            projectionClassOnRightSide: KClass<*>,
            propertyPath: String
        ): ProbablyInvertible.AssociationInvertibilityCheckResult {
            val otherSrcProp = entityClassOnRightSide.getPropertyAtPath(propertyPath)
            val otherSrcPropType = otherSrcProp.returnType.jvmErasure
            val otherPropType: KClass<*> = projectionClassOnRightSide.getPropertyAtPath(propertyPath).returnType.jvmErasure

            val srcProp = entityClass.memberProperties.first { it.name == propertyInfo.propertyName }

            val checkMappedBy by lazy {
                srcProp.getAnnotation<OneToMany>()?.let { it.mappedBy == propertyPath } == true
                        && otherSrcProp?.annotatedWith<ManyToOne>() == true
            }

            val isInversion = otherSrcPropType == entityClass
                    && entityClassOnRightSide == propertyInfo.srcPropTypeArgType2
                    && checkMappedBy

            val isProjectionTypeCompatible = isInversion
                    && projectionClassOnRightSide.isSubclassOf(propertyInfo.propTypeArgType2!!)
                    && projectionClass.isSubclassOf(otherPropType)

            return ProbablyInvertible.AssociationInvertibilityCheckResult(isInversion, isProjectionTypeCompatible)
        }
    }

    override fun fetch(
        entityManager: EntityManager,
        projectionIdentityMap: ProjectionIdentityMap
    ): List<Fetcher> {
        val fetchers = mutableListOf<Fetcher>()

        val cb = entityManager.criteriaBuilder
        val query = cb.createTupleQuery()
        val root = query.from(entityClass.java)
        val mapJoin = root.joinMap<Any, Any, Any>(propertyName, JoinType.LEFT)
        val mapJoinKey = mapJoin.key()
        val mapJoinValue = mapJoin.value()
        val keyRoot = (mapper.keyMapper as? EntityClassMapper)?.let { query.from(it.entityClass.java) }
        val valueRoot = (mapper.valueMapper as? EntityClassMapper)?.let { query.from(it.entityClass.java) }
        val keyPath = keyRoot ?: mapJoinKey
        val valuePath = valueRoot ?: mapJoinValue
        val tupleIndexCounter = TupleIndexCounter()
        query.multiselect(
            *mapper.idMapper.buildSelections(root, tupleIndexCounter).toTypedArray(),
            *mapper.keyMapper.buildSelections(keyPath, tupleIndexCounter).toTypedArray(),
            *mapper.valueMapper.buildSelections(valuePath, tupleIndexCounter).toTypedArray()
        )
        val projectionIds = projections.map { idProp.call(it) }.distinct()
        val predicates = listOfNotNull(
            root.get<Any>(idProp.name).`in`(projectionIds),
            (mapper.keyMapper as? EntityClassMapper)?.let { cb.equal(mapJoinKey, keyRoot) },
            (mapper.valueMapper as? EntityClassMapper)?.let { cb.equal(mapJoinValue, valueRoot) },
        )
        query.where(*predicates.toTypedArray())
        val tuples = entityManager.createQuery(query).resultList
        for (tuple in tuples) {
            val id = tuple[mapper.idMapper.tupleIndex]!!
            val projection = projectionIdentityMap.get(entityClass, projectionClassImpl, id)!!

            mapper.readTuple(tuple, projection, null, projectionIdentityMap).also {
                fetchers.addAll(it.first)
            }
        }

        return fetchers.toList()
    }

    override fun getReducer(): Fetcher.Reducer = Reducer

    private object Reducer : Fetcher.Reducer {
        override fun reduce(fetchers: List<Fetcher>): List<MapPropertyFetcher> {
            @Suppress("UNCHECKED_CAST")
            fetchers as List<MapPropertyFetcher>

            return fetchers.groupBy {
                Triple(it.entityClass, it.projectionClass, it.propertyName)
            }.values.map { similarFetchers ->
                if (similarFetchers.size == 1) {
                    similarFetchers.single()
                } else {
                    val sample = similarFetchers.first()
                    MapPropertyFetcher(
                        sample.projectorFactory,
                        sample.entityClass,
                        sample.projectionClass,
                        sample.projectionClassImpl,
                        sample.propertyName,
                        sample.propertyInfo,
                        similarFetchers.flatMap { it.projections }
                    )
                }
            }
        }
    }
}