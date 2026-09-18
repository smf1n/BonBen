package com.phonebench.bench

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

object CpuBench {

    /**
     * Нагружает все ядра CPU на durationMs миллисекунд.
     * Возвращает: (количество операций, операций в секунду)
     */
    suspend fun run(
        durationMs: Long = 5000,
        onProgress: (Int) -> Unit = {}
    ): Pair<Long, Long> = withContext(Dispatchers.Default) {

        val cores = Runtime.getRuntime().availableProcessors()
        val startTime = System.currentTimeMillis()
        val endTime = startTime + durationMs

        val jobs = (0 until cores).map { coreId ->
            async(Dispatchers.Default) {
                var ops = 0L
                var x = coreId * 1000.0 + 1.0

                while (System.currentTimeMillis() < endTime) {
                    repeat(10_000) {
                        x = sqrt(x * 1.0001 + 1.0)
                        x = x * 0.9999 + 0.0001
                        ops++
                    }
                }
                ops
            }
        }

        // Прогресс
        val progressJob = async {
            while (System.currentTimeMillis() < endTime) {
                val p = ((System.currentTimeMillis() - startTime) * 100 / durationMs).toInt()
                onProgress(p.coerceIn(0, 100))
                delay(100)
            }
        }

        val totalOps = jobs.awaitAll().sum()
        progressJob.await()

        val elapsedSec = (System.currentTimeMillis() - startTime).coerceAtLeast(1) / 1000.0
        val opsPerSec = (totalOps / elapsedSec).toLong()

        Pair(totalOps, opsPerSec)
    }

    /**
     * Считает баллы CPU.
     * 100 000 000 ops/sec = 10000 баллов (примерно Snapdragon 8 Gen 2)
     */
    fun calcScore(opsPerSec: Long): Long {
        return (opsPerSec / 10_000).coerceIn(100, 100_000)
    }
}
