package com.appunite.mockwebserverextensions.util

class MultipleFailuresError(private val heading: String, val failures: List<Throwable>) :
    AssertionError(heading, failures.getOrNull(0)) {
    init {
        require(heading.isNotBlank()) { "Heading should not be blank" }
    }

    override val message: String
        get() = buildString {
            append(heading)
            append(" (")
            append(failures.size).append(" ")
            append(
                when (failures.size) {
                    0 -> "no failures"
                    1 -> "failure"
                    else -> "failures"
                }
            )
            append(")")
            append("\n")

            failures.joinTo(this, separator = "\n") {
                nullSafeMessage(it).lines().joinToString(separator = "\n") { "\t$it" }
            }
        }

    private fun nullSafeMessage(failure: Throwable): String =
        failure.javaClass.name + ": " + failure.message.orEmpty().ifBlank { "<no message>" }
}
