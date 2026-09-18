package com.phonebench.bench

data class BenchResult(
    val cpuScore: Long = 0,
    val ramScore: Long = 0,
    val storageScore: Long = 0,
    val gpuScore: Long = 0,
    val totalScore: Long = 0,
    val cpuOpsPerSec: Long = 0,
    val ramMbPerSec: Long = 0,
    val storageMbPerSec: Double = 0.0,
    val gpuFps: Double = 0.0,
    val rating: String = "",
    val deviceName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
