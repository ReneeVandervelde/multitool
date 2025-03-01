package com.reneevandervelde.system.processes

import com.inkapplications.standard.CompositeException
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.datetime.Clock
import kotlin.time.Duration

class OperationRunner(
    val runScope: CoroutineScope,
    val logger: KimchiLogger,
    val clock: Clock,
) {
    suspend fun run(operations: List<Operation>)
    {
        val results = operations.map { operation ->
            runScope.async {
                when (val decision = operation.enabled()) {
                    is Decision.No -> OperationRunResult.Skipped(operation, decision)
                    is Decision.Yes -> {
                        val start = clock.now()
                        runCatching {
                            operation.also {
                                logger.debug("[${operation.name}] running - ${decision.rationale}")
                                it.runOperation()
                            }
                        }.fold(
                            onSuccess = {
                                OperationRunResult.Success(operation, clock.now() - start)
                            },
                            onFailure = {
                                OperationRunResult.Failure(operation, clock.now() - start, it)
                            }
                        )
                    }
                }
            }
        }.awaitAll()

        results.forEach {
            when (it) {
                is OperationRunResult.Skipped -> {
                    logger.info("[${it.operation.name}] SKIPPED: ${it.decision.rationale}")
                }
                is OperationRunResult.Success -> {
                    logger.info("[${it.operation.name}] COMPLETED in ${it.runTime}")
                }
                is OperationRunResult.Failure -> {
                    logger.info("[${it.operation.name}] FAILED in ${it.runTime}")
                }
            }
        }

        val failures = results.filterIsInstance<OperationRunResult.Failure>()

        when {
            failures.size == 1 -> {
                throw failures.single().error
            }
            failures.size > 1 -> {
                logger.debug("${failures.size} operations failed")
                throw CompositeException(failures.map { it.error })
            }
            else -> {
                logger.debug("Updates Completed")
            }
        }
    }

    sealed interface OperationRunResult
    {
        data class Skipped(val operation: Operation, val decision: Decision.No): OperationRunResult
        data class Success(val operation: Operation, val runTime: Duration): OperationRunResult
        data class Failure(val operation: Operation, val runTime: Duration, val error: Throwable): OperationRunResult
    }
}
