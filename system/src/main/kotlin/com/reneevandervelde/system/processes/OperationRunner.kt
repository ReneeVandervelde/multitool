package com.reneevandervelde.system.processes

import com.inkapplications.standard.CompositeException
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class OperationRunner(
    val runScope: CoroutineScope,
    val logger: KimchiLogger,
) {
    suspend fun run(operations: List<Operation>)
    {
        val results = operations
            .filter { operation ->
                when (val decision = operation.enabled()) {
                    is Decision.No -> false.also {
                        logger.info("[${operation.name}] disabled: ${decision.rationale}")
                    }
                    is Decision.Yes -> true.also {
                        logger.info("[${operation.name}] enabled: ${decision.rationale}")
                    }
                }
            }
            .map { operation ->
                runScope.async {
                    runCatching {
                        operation.runOperation()
                    }.also {
                        logger.info("[${operation.name}] completed")
                    }
                }
            }
            .awaitAll()

        val failures = results.filter { it.isFailure }

        when {
            failures.size == 1 -> {
                throw failures.single().exceptionOrNull()!!
            }
            failures.size > 1 -> {
                logger.debug("${failures.size} operations failed")
                throw CompositeException(failures.map { it.exceptionOrNull()!! })
            }
            else -> {
                logger.debug("${operations.size} operations completed successfully")
            }
        }
    }
}
