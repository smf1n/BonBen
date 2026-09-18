package com.phonebench.bench

import android.content.Context
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BenchmarkRunner {

    /**
     * Запускает все тесты.
     * onStep — callback: (название шага, прогресс 0-100)
     */
    suspend fun runAll(
        context: Context,
        onStep: (String, Int) -> Unit
    ): BenchResult = withContext(Dispatchers.Default) {

        var cpuScore = 0L
        var ramScore = 0L
        var storageScore = 0L
        var gpuScore = 0L
        var cpuOps = 0L
        var ramMb = 0.0
        var storageMb = 0.0
        var gpuFps = 0.0

        // === CPU ===
        onStep("CPU", 0)
        val (_, opsPerSec) = CpuBench.run(5000) { p ->
            onStep("CPU", p)
        }
        cpuOps = opsPerSec
        cpuScore = CpuBench.calcScore(opsPerSec)
        onStep("CPU", 100)

        // === RAM ===
        onStep("RAM", 0)
        ramMb = RamBench.run(context) { p ->
            onStep("RAM", p)
        }
        ramScore = RamBench.calcScore(ramMb)
        onStep("RAM", 100)

        // === Storage ===
        onStep("Storage", 0)
        storageMb = StorageBench.run(context) { p ->
            onStep("Storage", p)
        }
        storageScore = StorageBench.calcScore(storageMb)
        onStep("Storage", 100)

        // GPU тест запускается отдельно (через Activity)
        // Здесь просто базовая заглушка
        gpuFps = 30.0
        gpuScore = GpuBenchRenderer.calcScore(gpuFps)
        onStep("GPU", 100)

        val total = cpuScore + ramScore + storageScore + gpuScore

        val rating = when {
            total > 40000 -> "🔥 Флагман"
            total > 25000 -> "⚡ Очень мощный"
            total > 15000 -> "✅ Мощный"
            total > 8000 -> "🟡 Средний"
            total > 4000 -> "🟠 Слабый"
            else -> "🔴 Очень слабый"
        }

        BenchResult(
            cpuScore = cpuScore,
            ramScore = ramScore,
            storageScore = storageScore,
            gpuScore = gpuScore,
            totalScore = total,
            cpuOpsPerSec = cpuOps,
            ramMbPerSec = ramMb.toLong(),
            storageMbPerSec = storageMb,
            gpuFps = gpuFps,
            rating = rating,
            deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
        )
    }
}
