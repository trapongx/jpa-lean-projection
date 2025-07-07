package com.runninglane.jpa.projection.test.cases.elementcollection.map

import javax.persistence.*

@Entity
class ComplexEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var string: String? = null
    @ManyToOne
    var associated: SimpleEntity? = null

    override fun hashCode(): Int {
        return id?.hashCode() ?: super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComplexEntity) return false
        if (id == null || other.id == null) {
            return false
        }
        return id == other.id
    }
}