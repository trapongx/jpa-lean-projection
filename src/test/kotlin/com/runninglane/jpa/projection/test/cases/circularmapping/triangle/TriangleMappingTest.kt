package com.runninglane.jpa.projection.test.cases.circularmapping.triangle

import com.runninglane.jpa.projection.queryWithProjection
import com.runninglane.jpa.projection.test.BaseTest
import com.runninglane.jpa.projection.test.cases.circularmapping.triangle.model.AEntity
import com.runninglane.jpa.projection.test.cases.circularmapping.triangle.model.AProjection
import com.runninglane.jpa.projection.test.cases.circularmapping.triangle.model.BEntity
import com.runninglane.jpa.projection.test.cases.circularmapping.triangle.model.BProjection
import com.runninglane.jpa.projection.test.cases.circularmapping.triangle.model.CEntity
import com.runninglane.jpa.projection.test.cases.circularmapping.triangle.model.CProjection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [TriangleMappingTestConfig::class])
class TriangleMappingTest : BaseTest() {

    @Test
    fun `should correctly project tri-circular properties`() {
        val aEntity = AEntity().apply {
            name = "Test1"
        }.also { aEntity -> entityManager.persist(aEntity) }

        val bEntity = BEntity().apply {
            name = "Test2"
        }.also { bEntity -> entityManager.persist(bEntity) }

        val cEntity = CEntity().apply {
            name = "Test3"
        }.also { cEntity -> entityManager.persist(cEntity) }

        entityManager.flush()
        entityManager.clear()

        run {
            val aProjection = entityManager.queryWithProjection<AEntity, AProjection>().single()
            assertThat(aProjection.name).isEqualTo(aEntity.name)

            val bProjection = entityManager.queryWithProjection<BEntity, BProjection>().single()
            assertThat(bProjection.name).isEqualTo(bEntity.name)

            val eProjection = entityManager.queryWithProjection<CEntity, CProjection>().single()
            assertThat(eProjection.name).isEqualTo(cEntity.name)
        }

        aEntity.b = bEntity
        entityManager.merge(aEntity)

        bEntity.c = cEntity
        entityManager.merge(bEntity)

        cEntity.a = aEntity
        entityManager.merge(cEntity)

        entityManager.flush()
        entityManager.clear()

        run {
            val aProjection = entityManager.queryWithProjection<AEntity, AProjection>().single()
            assertThat(aProjection.name).isEqualTo(aEntity.name)
            assertThat(aProjection.b?.name).isEqualTo(bEntity.name)
            assertThat(aProjection.b?.c?.name).isEqualTo(cEntity.name)

            val bProjection = entityManager.queryWithProjection<BEntity, BProjection>().single()
            assertThat(bProjection.name).isEqualTo(bEntity.name)
            assertThat(bProjection.c?.name).isEqualTo(cEntity.name)
            assertThat(bProjection.c?.a?.name).isEqualTo(aEntity.name)

            val eProjection = entityManager.queryWithProjection<CEntity, CProjection>().single()
            assertThat(eProjection.name).isEqualTo(cEntity.name)
            assertThat(eProjection.a?.name).isEqualTo(aEntity.name)
            assertThat(eProjection.a?.b?.name).isEqualTo(bEntity.name)
        }
    }

}
