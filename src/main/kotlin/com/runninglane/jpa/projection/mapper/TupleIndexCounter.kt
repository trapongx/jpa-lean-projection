package com.runninglane.jpa.projection.mapper

class TupleIndexCounter {
    private var nextIndex = 0

    fun next(): Int = nextIndex++
}