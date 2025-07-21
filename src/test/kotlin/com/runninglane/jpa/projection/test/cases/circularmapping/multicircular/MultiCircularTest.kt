package com.runninglane.jpa.projection.test.cases.circularmapping.multicircular

import com.runninglane.jpa.projection.queryWithProjection
import com.runninglane.jpa.projection.test.BaseTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

interface LikeAWithVar {
    val id: Long
    var lateinit1: String
    var null1: String?
    var null2: String?
    var noneNull: Int
}

interface LikeBWithVar {
    val id: Long
    var lateinit1: String
    var null1: String?
    var null2: String?
    var noneNull: Int
}

interface LikeCWithVar {
    val id: Long
    var lateinit1: String
    var null1: String?
    var null2: String?
    var noneNull: Int
    var d: LikeDWithVar
}

interface LikeDWithVar {
    val id: Long
    var aList: List<LikeAWithVar>
    var bSet: Set<LikeBWithVar>?
    var cList: List<LikeCWithVar>
}

interface LikeAWithVal {
    val id: Long
    val lateinit1: String
    val null1: String?
    val null2: String?
    val noneNull: Int
}

interface LikeBWithVal {
    val id: Long
    val lateinit1: String
    val null1: String?
    val null2: String?
    val noneNull: Int
}

interface LikeCWithVal {
    val id: Long
    val lateinit1: String
    val null1: String?
    val null2: String?
    val noneNull: Int
    val d: LikeDWithVal
}

interface LikeDWithVal {
    val id: Long
    val aList: List<LikeAWithVal>
    val bSet: Set<LikeBWithVal>?
    val cList: List<LikeCWithVal>
}

@DataJpaTest
@ContextConfiguration(classes = [MultiCircularTestConfig::class])
class Test : BaseTest() {
    lateinit var a1: A
    lateinit var a2: A
    lateinit var a3: A
    lateinit var a4: A
    lateinit var b1: B
    lateinit var b2: B
    lateinit var b3: B
    lateinit var b4: B
    lateinit var c1: C
    lateinit var c2: C
    lateinit var c3: C
    lateinit var c4: C
    lateinit var d1: D
    lateinit var d2: D

    @BeforeEach
    fun buildModel() {
        fun makeA(i: Int, noneNull: Int) = A().also {
            it.lateinit1 = "fake code $i"
            it.null1 = "but have value"
            it.null2 = null
            it.noneNull = noneNull
            entityManager.persist(it)
        }
        fun makeB(i: Int, noneNull: Int) = B().also {
            it.lateinit1 = "fake code $i"
            it.null1 = "but have value"
            it.null2 = null
            it.noneNull = noneNull
            entityManager.persist(it)
        }
        fun makeC(i: Int, noneNull: Int, d: D) = C().also {
            it.lateinit1 = "fake code $i"
            it.null1 = "but have value"
            it.null2 = null
            it.noneNull = noneNull
            it.d = d
            entityManager.persist(it)
        }
        fun makeD(aList: List<A>, bSet: Set<B>) = D().also {
            it.aList.addAll(aList)
            it.bSet = bSet.toMutableSet()
            entityManager.persist(it)
        }
        a1 = makeA(1, 1)
        a2 = makeA(2, 2)
        a3 = makeA(3, 3)
        a4 = makeA(4, 4)
        b1 = makeB(1, 1)
        b2 = makeB(2, 2)
        b3 = makeB(3, 3)
        b4 = makeB(4, 4)
        d1 = makeD(listOf(a1, a2), setOf(b1, b2))
        d2 = makeD(listOf(a3, a4), setOf(b3, b4))
        c1 = makeC(1, 1, d1)
        c2 = makeC(2, 2, d1)
        c3 = makeC(3, 3, d2)
        c4 = makeC(4, 4, d2)
        d1 = entityManager.find(D::class.java, d1.id!!)
        d2 = entityManager.find(D::class.java, d2.id!!)
    }

    @Test
    fun testVar() {
        entityManager.queryWithProjection<D, LikeDWithVar>(
             { cb, _, root ->
                cb.equal(root.get<Any>("id"), d1.id)
            }
        ).also { list ->
            Assertions.assertThat(list.size).isEqualTo(1)
            val likeD = list.single()
            Assertions.assertThat(likeD.aList.size).isEqualTo(2)
            val likeA1 = likeD.aList[0]
            val likeA2 = likeD.aList[1]
            Assertions.assertThat(likeD.bSet!!.size).isEqualTo(2)
            val likeB1 = likeD.bSet!!.single { it.lateinit1 == b1.lateinit1 }
            val likeB2 = likeD.bSet!!.single { it.lateinit1 == b2.lateinit1 }
            Assertions.assertThat(likeD.cList.size).isEqualTo(2)
            val likeC1 = likeD.cList[0]
            val likeC2 = likeD.cList[1]
            Assertions.assertThat(likeA1.lateinit1).isEqualTo(a1.lateinit1)
            Assertions.assertThat(likeA1.null1).isEqualTo(a1.null1)
            Assertions.assertThat(likeA1.null2).isEqualTo(a1.null2)
            Assertions.assertThat(likeA1.noneNull).isEqualTo(a1.noneNull)
            Assertions.assertThat(likeA2.lateinit1).isEqualTo(a2.lateinit1)
            Assertions.assertThat(likeA2.null1).isEqualTo(a2.null1)
            Assertions.assertThat(likeA2.null2).isEqualTo(a2.null2)
            Assertions.assertThat(likeA2.noneNull).isEqualTo(a2.noneNull)
            Assertions.assertThat(likeB1.lateinit1).isEqualTo(b1.lateinit1)
            Assertions.assertThat(likeB1.null1).isEqualTo(b1.null1)
            Assertions.assertThat(likeB1.null2).isEqualTo(b1.null2)
            Assertions.assertThat(likeB1.noneNull).isEqualTo(b1.noneNull)
            Assertions.assertThat(likeB2.lateinit1).isEqualTo(b2.lateinit1)
            Assertions.assertThat(likeB2.null1).isEqualTo(b2.null1)
            Assertions.assertThat(likeB2.null2).isEqualTo(b2.null2)
            Assertions.assertThat(likeB2.noneNull).isEqualTo(b2.noneNull)
            Assertions.assertThat(likeC1.lateinit1).isEqualTo(c1.lateinit1)
            Assertions.assertThat(likeC1.null1).isEqualTo(c1.null1)
            Assertions.assertThat(likeC1.null2).isEqualTo(c1.null2)
            Assertions.assertThat(likeC1.noneNull).isEqualTo(c1.noneNull)
            Assertions.assertThat(likeC1.d.id).isEqualTo(d1.id)
            Assertions.assertThat(likeC2.lateinit1).isEqualTo(c2.lateinit1)
            Assertions.assertThat(likeC2.null1).isEqualTo(c2.null1)
            Assertions.assertThat(likeC2.null2).isEqualTo(c2.null2)
            Assertions.assertThat(likeC2.noneNull).isEqualTo(c2.noneNull)
            Assertions.assertThat(likeC1.d === likeD).isTrue()
            Assertions.assertThat(likeC2.d === likeD).isTrue()
        }
    }

    @Test
    fun testVal() {
        entityManager.queryWithProjection<D, LikeDWithVal>(
            { cb, _, root ->
                cb.equal(root.get<Any>("id"), d2.id)
            }
        ).also { list ->
            Assertions.assertThat(list.size).isEqualTo(1)
            val likeD = list.single()
            Assertions.assertThat(likeD.aList.size).isEqualTo(2)
            val likeA3 = likeD.aList[0]
            val likeA4 = likeD.aList[1]
            Assertions.assertThat(likeD.bSet!!.size).isEqualTo(2)
            val likeB3 = likeD.bSet!!.single { it.lateinit1 == b3.lateinit1 }
            val likeB4 = likeD.bSet!!.single { it.lateinit1 == b4.lateinit1 }
            Assertions.assertThat(likeD.cList.size).isEqualTo(2)
            val likeC3 = likeD.cList[0]
            val likeC4 = likeD.cList[1]
            Assertions.assertThat(likeA3.lateinit1).isEqualTo(a3.lateinit1)
            Assertions.assertThat(likeA3.null1).isEqualTo(a3.null1)
            Assertions.assertThat(likeA3.null2).isEqualTo(a3.null2)
            Assertions.assertThat(likeA3.noneNull).isEqualTo(a3.noneNull)
            Assertions.assertThat(likeA4.lateinit1).isEqualTo(a4.lateinit1)
            Assertions.assertThat(likeA4.null1).isEqualTo(a4.null1)
            Assertions.assertThat(likeA4.null2).isEqualTo(a4.null2)
            Assertions.assertThat(likeA4.noneNull).isEqualTo(a4.noneNull)
            Assertions.assertThat(likeB3.lateinit1).isEqualTo(b3.lateinit1)
            Assertions.assertThat(likeB3.null1).isEqualTo(b3.null1)
            Assertions.assertThat(likeB3.null2).isEqualTo(b3.null2)
            Assertions.assertThat(likeB3.noneNull).isEqualTo(b3.noneNull)
            Assertions.assertThat(likeB4.lateinit1).isEqualTo(b4.lateinit1)
            Assertions.assertThat(likeB4.null1).isEqualTo(b4.null1)
            Assertions.assertThat(likeB4.null2).isEqualTo(b4.null2)
            Assertions.assertThat(likeB4.noneNull).isEqualTo(b4.noneNull)
            Assertions.assertThat(likeC3.lateinit1).isEqualTo(c3.lateinit1)
            Assertions.assertThat(likeC3.null1).isEqualTo(c3.null1)
            Assertions.assertThat(likeC3.null2).isEqualTo(c3.null2)
            Assertions.assertThat(likeC3.noneNull).isEqualTo(c3.noneNull)
            Assertions.assertThat(likeC3.d.id).isEqualTo(d2.id)
            Assertions.assertThat(likeC4.lateinit1).isEqualTo(c4.lateinit1)
            Assertions.assertThat(likeC4.null1).isEqualTo(c4.null1)
            Assertions.assertThat(likeC4.null2).isEqualTo(c4.null2)
            Assertions.assertThat(likeC4.noneNull).isEqualTo(c4.noneNull)
            Assertions.assertThat(likeC3.d === likeD).isTrue()
            Assertions.assertThat(likeC4.d === likeD).isTrue()
        }
    }
}