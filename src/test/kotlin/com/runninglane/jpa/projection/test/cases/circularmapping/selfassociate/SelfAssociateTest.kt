package com.runninglane.jpa.projection.test.cases.circularmapping.selfassociate

import com.runninglane.jpa.projection.queryWithProjection
import com.runninglane.jpa.projection.test.BaseTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration


interface LikeA {
    var id: Long?
    var parent: LikeA?
}

@DataJpaTest
@ContextConfiguration(classes = [SelfAssociateTestConfig::class])
class SelfAssociateTest : BaseTest() {
    lateinit var aAlone: A
    lateinit var aParent: A
    lateinit var aChild1: A
    lateinit var aChild2: A

    @BeforeEach
    fun buildModel() {
        aAlone = A().also {
            entityManager.persist(it)
        }
        aParent = A().also {
            entityManager.persist(it)
        }
        aChild1 = A().also {
            it.parent = aParent
            entityManager.persist(it)
        }
        aChild2 = A().also {
            it.parent = aParent
            entityManager.persist(it)
        }
    }

    @Test
    fun test() {
        entityManager.queryWithProjection<A, LikeA>().also { list ->
            Assertions.assertThat(list.size).isEqualTo(4)
            val likeAAlone = list.single { it.id == aAlone.id }
            val likeAChild1 = list.single { it.id == aChild1.id }
            val likeAChild2 = list.single { it.id == aChild2.id }
            val likeAParent = list.single { it.id == aParent.id }
            Assertions.assertThat(likeAAlone.parent).isNull()
            Assertions.assertThat(likeAParent.parent).isNull()
            Assertions.assertThat(likeAChild1.parent).isEqualTo(likeAParent)
            Assertions.assertThat(likeAChild2.parent).isEqualTo(likeAParent)
        }
    }

}