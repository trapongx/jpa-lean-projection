package com.runninglane.jpa.projection.string

import java.util.Locale.*

/**
 * Extension function to capitalize the first character of a string
 */
internal fun String.capitalizeFirst(): String = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(getDefault())
    else it.toString()
}
