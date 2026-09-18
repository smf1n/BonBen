package com.phonebench.bench

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile

object StorageBench {

    /**
     * Записывает и читает файл.
     * Возвращает: МБ/сек средней скорости.
     */
    suspend fun run(
        context: Context,
        sizeMb: Int = 64,
        onProgress: (Int) -> Unit = {}
    ): Double = withContext(Dispatchers.IO) {

        val dir = context.cacheDir
        val file = File(dir, "bench_test.bin")
        val bytes = ByteArray(1024 * 1024) { (it and 0xFF).toByte() }

        // Запись
        val writeStart = System.currentTimeMillis()
        FileOutputStream(file).use { fos ->
            for (i in 0 until sizeMb) {
                fos.write(bytes)
                onProgress((i * 50 / sizeMb))
            }
        }
        val writeMs = (System.currentTimeMillis() - writeStart).coerceAtLeast(1)

        // Чтение
        val readStart = System.currentTimeMillis()
        RandomAccessFile(file, "r").use { raf ->
            val buf = ByteArray(1024 * 1024)
            for (i in 0 until sizeMb) {
                raf.readFully(buf)
                onProgress(50 + (i * 50 / sizeMb))
            }
        }
        val readMs = (System.currentTimeMillis() - readStart).coerceAtLeast(1)

        file.delete()

        val writeSpeed = sizeMb.toDouble() / (writeMs / 1000.0)
        val readSpeed = sizeMb.toDouble() / (readMs / 1000.0)

        (writeSpeed + readSpeed) / 2.0
    }

    /**
     * Считает баллы Storage.
     * 500 МБ/сек = 5000 баллов
     */
    fun calcScore(mbPerSec: Double): Long {
        return (mbPerSec * 10).toLong().coerceIn(100, 50_000)
    }
}
