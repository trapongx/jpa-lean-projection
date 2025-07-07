package com.runninglane.jpa.projection.mapper

import com.runninglane.jpa.projection.ProjectionIdentityMap
import javax.persistence.EntityManager

internal interface Fetcher {
    /**
     * Execute the fetch.
     * @return List of fetchers for further fetching.
     */
    fun fetch(entityManager: EntityManager, projectionIdentityMap: ProjectionIdentityMap): List<Fetcher>

    /**
     * Fetcher of the same class must return the same reducer.
     */
    fun getReducer(): Reducer

    interface Reducer {
        fun reduce(fetchers: List<Fetcher>): List<Fetcher>
    }
}