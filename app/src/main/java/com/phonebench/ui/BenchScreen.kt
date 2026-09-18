package com.phonebench.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonebench.bench.BenchResult
import com.phonebench.bench.BenchmarkRunner
import com.phonebench.util.DeviceInfo
import kotlinx.coroutines.launch

@Composable
fun BenchScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isRunning by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0) }
    var result by remember { mutableStateOf<BenchResult?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A0E27), Color(0xFF1A1F3A))
                )
            )
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))

        Text(
            text = "⚡ BonBen",
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4ADE80)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Стресс-тест твоего телефона",
            fontSize = 14.sp,
            color = Color(0xFFAAAAAA)
        )

        Spacer(Modifier.height(30.dp))

        DeviceCard()

        Spacer(Modifier.height(20.dp))

        if (!isRunning && result == null) {
            Button(
                onClick = {
                    isRunning = true
                    result = null
                    scope.launch {
                        result = BenchmarkRunner.runAll(context) { step, p ->
                            currentStep = step
                            progress = p
                        }
                        isRunning = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4ADE80),
                    contentColor = Color(0xFF0A0E27)
                )
            ) {
                Text("🚀 НАЧАТЬ ТЕСТ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (isRunning) {
            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131A3A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = "Тест: $currentStep",
                        color = Color(0xFFE6EDF3),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = progress / 100f,
                        modifier = Modifier.fillMaxWidth().height(12.dp),
                        color = Color(0xFF4ADE80),
                        trackColor = Color(0xFF2A3050)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "$progress%",
                        color = Color(0xFFAAAAAA),
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (result != null) {
            Spacer(Modifier.height(20.dp))
            ResultCard(result!!)

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    result = null
                    progress = 0
                    currentStep = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4FACFE),
                    contentColor = Color(0xFF0A0E27)
                )
            ) {
                Text("🔄 ПОВТОРИТЬ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun DeviceCard() {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131A3A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "📱 Устройство",
                color = Color(0xFF4ADE80),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            InfoRow("Модель", DeviceInfo.getModel())
            InfoRow("Система", DeviceInfo.getAndroidVersion())
            InfoRow("Ядер CPU", DeviceInfo.getCpuCores().toString())
            InfoRow("Архитектура", DeviceInfo.getCpuAbi())
            InfoRow("RAM всего", DeviceInfo.formatGb(DeviceInfo.getTotalRamGb(context)))
            InfoRow("RAM свободно", DeviceInfo.formatGb(DeviceInfo.getAvailableRamGb(context)))
            InfoRow("Память всего", DeviceInfo.formatGb(DeviceInfo.getTotalStorageGb()))
            InfoRow("Память свободно", DeviceInfo.formatGb(DeviceInfo.getFreeStorageGb()))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFFAAAAAA), fontSize = 13.sp)
        Text(value, color = Color(0xFFE6EDF3), fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ResultCard(result: BenchResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131A3A)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆 РЕЗУЛЬТАТ",
                color = Color(0xFF4ADE80),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = result.totalScore.toString(),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4FACFE)
            )

            Text(
                text = "баллов",
                color = Color(0xFFAAAAAA),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = result.rating,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE6EDF3)
            )

            Spacer(Modifier.height(20.dp))

            Divider(color = Color(0xFF2A3050))

            Spacer(Modifier.height(16.dp))

            ScoreRow("🔲 CPU", result.cpuScore, "${result.cpuOpsPerSec / 1_000_000}M ops/s")
            ScoreRow("💾 RAM", result.ramScore, "${result.ramMbPerSec} МБ/с")
            ScoreRow("💿 Storage", result.storageScore, "${"%.0f".format(result.storageMbPerSec)} МБ/с")
            ScoreRow("🎮 GPU", result.gpuScore, "${"%.1f".format(result.gpuFps)} FPS")
        }
    }
}

@Composable
fun ScoreRow(label: String, score: Long, detail: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, color = Color(0xFFE6EDF3), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(detail, color = Color(0xFF888888), fontSize = 12.sp)
        }
        Text(
            text = score.toString(),
            color = Color(0xFF4ADE80),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
