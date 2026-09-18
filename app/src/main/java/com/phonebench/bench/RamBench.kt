package com.phonebench.bench

import android.app.ActivityManager
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RamBench {

    /**
     * Заполняет память массивами и читает обратно.
     * Возвращает: сколько МБ/сек успели обработать.
     */
    suspend fun run(
        context: Context,
        maxMb: Int = 256,
        onProgress: (Int) -> Unit = {}
    ): Double = withContext(Dispatchers.Default) {

        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)

        // Берём не больше, чем 50% от доступной памяти
        val availableMb = (mi.availMem / (1024 * 1024)).toInt()
        val targetMb = minOf(maxMb, (availableMb * 0.5).toInt()).coerceAtLeast(32)

        val startTime = System.currentTimeMillis()
        var processedMb = 0
        val blocks = mutableListOf<ByteArray>()

        try {
            val blockSizeMb = 16
            val iterations = (targetMb / blockSizeMb).coerceAtLeast(1)

            for (i in 0 until iterations) {
                val block = ByteArray(blockSizeMb * 1024 * 1024) { (it and 0xFF).toByte() }
                blocks.add(block)

                // Читаем обратно
                var sum = 0L
                for (j in block.indices step 4096) {
                    sum += block[j]
                }

                processedMb += blockSizeMb
                val progress = ((i + 1) * 100) / iterations
                onProgress(progress)
            }
        } catch (e: OutOfMemoryError) {
            // Ок, память кончилась — значит, тест выполнен
        }

        val elapsedSec = (System.currentTimeMillis() - startTime).coerceAtLeast(1) / 1000.0

        // Очищаем
        blocks.clear()
        System.gc()

        processedMb / elapsedSec
    }

    /**
     * Считает баллы RAM.
     * 500 МБ/сек = 5000 баллов
     */
    fun calcScore(mbPerSec: Double): Long {
        return (mbPerSec * 10).toLong().coerceIn(100, 50_000)
    }
}
