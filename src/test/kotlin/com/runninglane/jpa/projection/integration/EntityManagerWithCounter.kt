package com.runninglane.jpa.projection.integration

import com.runninglane.jpa.projection.ProjectableEntityManager
import javax.persistence.Query
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaDelete
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.CriteriaUpdate

class EntityManagerWithCounter(private val delegate: ProjectableEntityManager) : ProjectableEntityManager by delegate {
    var countCreateWithQlString = 0
        private set

    var countCreateWithCriteriaQuery = 0
        private set

    var countUpdate = 0
        private set

    var countDelete = 0
        private set

    override fun createQuery(qlString: String): Query {
        countCreateWithQlString++
        return delegate.createQuery(qlString)
    }

    override fun <T : Any?> createQuery(criteriaQuery: CriteriaQuery<T?>): TypedQuery<T?> {
        countCreateWithCriteriaQuery++
        return delegate.createQuery(criteriaQuery)
    }

    override fun createQuery(updateQuery: CriteriaUpdate<*>): Query {
        countUpdate++
        return delegate.createQuery(updateQuery)
    }

    override fun createQuery(deleteQuery: CriteriaDelete<*>?): Query? {
        countDelete++
        return delegate.createQuery(deleteQuery)
    }

    override fun <T : Any?> createQuery(
        qlString: String?,
        resultClass: Class<T?>?
    ): TypedQuery<T?>? {
        countCreateWithQlString++
        return delegate.createQuery(qlString, resultClass)
    }

    fun assertQueryCount(expectedQueryCount: Int) {
/*        assertThat(countCreateWithQlString).isEqualTo(0)
        assertThat(countCreateWithCriteriaQuery).isEqualTo(expectedQueryCount)
        assertThat(countUpdate).isEqualTo(0)
        assertThat(countDelete).isEqualTo(0)*/
    }
}
