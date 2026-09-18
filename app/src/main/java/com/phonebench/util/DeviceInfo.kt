package com.phonebench.util

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import java.util.Locale

object DeviceInfo {

    fun getModel(): String = "${Build.MANUFACTURER} ${Build.MODEL}"

    fun getAndroidVersion(): String = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

    fun getCpuCores(): Int = Runtime.getRuntime().availableProcessors()

    fun getCpuAbi(): String = Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"

    fun getTotalRamGb(context: Context): Double {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return mi.totalMem.toDouble() / (1024.0 * 1024.0 * 1024.0)
    }

    fun getAvailableRamGb(context: Context): Double {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return mi.availMem.toDouble() / (1024.0 * 1024.0 * 1024.0)
    }

    fun getTotalStorageGb(): Double {
        val stat = StatFs(Environment.getDataDirectory().path)
        return (stat.blockCountLong * stat.blockSizeLong).toDouble() / (1024.0 * 1024.0 * 1024.0)
    }

    fun getFreeStorageGb(): Double {
        val stat = StatFs(Environment.getDataDirectory().path)
        return (stat.availableBlocksLong * stat.blockSizeLong).toDouble() / (1024.0 * 1024.0 * 1024.0)
    }

    fun formatGb(value: Double): String =
        String.format(Locale.US, "%.1f ГБ", value)
}
