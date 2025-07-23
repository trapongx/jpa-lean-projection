package com.runninglane.jpa.projection.integration.cases.elementcollection.map

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class SimpleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var string: String? = null

    override fun hashCode(): Int {
        return id?.hashCode() ?: super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleEntity) return false
        if (id == null || other.id == null) {
            return false
        }
        return id == other.id
    }
}