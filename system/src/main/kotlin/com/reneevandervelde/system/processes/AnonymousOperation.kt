package com.reneevandervelde.system.processes

data class AnonymousOperation(
    override val name: String,
    val action: suspend () -> Unit,
    val predicate: suspend () -> Boolean = { true },
): Operation {
    override suspend fun enabled(): Decision {
        return if (predicate()) {
            Decision.Yes("Enabled by predicate")
        } else {
            Decision.No("Disabled by predicate")
        }
    }

    override suspend fun runOperation() {
        action()
    }
}

fun ShellCommand.asOperation(
    predicate: suspend () -> Boolean = { true },
) = AnonymousOperation(
    name = name,
    action = {
        exec(capture = true)
            .printCapturedLines(name)
            .awaitSuccess()
    },
    predicate = predicate,
)

