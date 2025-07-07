package com.runninglane.jpa.projection.mapper

@Suppress("UNCHECKED_CAST")
internal fun List<Fetcher>.reduce(): List<Fetcher> {
    return this.groupBy { it::class }.values.flatMap { fetchersWithSameClass ->
        fetchersWithSameClass.first().getReducer().reduce(fetchersWithSameClass)
    }
}