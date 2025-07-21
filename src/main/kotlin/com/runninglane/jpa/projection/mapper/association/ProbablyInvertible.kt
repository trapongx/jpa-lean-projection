package com.runninglane.jpa.projection.mapper.association

import kotlin.reflect.KClass

internal interface ProbablyInvertible {

    class AssociationInvertibilityCheckResult(val isInvertible: Boolean, val isProjectionTypeCompatible: Boolean)

    /**
     * Picture the bidirectional relationship between two entity classes.
     * LEFT --[propertyPath]--> RIGHT
     * @param entityClassOnRightSide The entity class on the right side of the association.
     * @param projectionClassOnRightSide The projection class on the right side of the association.
     * @param propertyPath The name of the property on the left side of the association that causes relationship.
     */
    fun checkAssociationInvertibility(
        entityClassOnRightSide: KClass<*>,
        projectionClassOnRightSide: KClass<*>,
        propertyPath: String
    ): AssociationInvertibilityCheckResult
}