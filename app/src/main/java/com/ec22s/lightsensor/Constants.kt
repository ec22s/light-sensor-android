package com.ec22s.lightsensor

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val title = "明るさ表示"
const val energyGaugeCutRate = .3f
const val energyGaugeWidth = 35f
const val energyGaugeDurationMSec = 1000
const val labelUpdate = "更新間隔"
const val formatLightLevel = "%.1f"
const val unitLightLevel = "lux"
const val unitUpdate = "秒"
const val sliderMax = 60

val mainSize = 256.dp
val paddingMainBottom = 16.dp
val paddingSwitchTop = 16.dp
val paddingSwitchLeft = 16.dp
val paddingSensorInfoTop = 32.dp
val paddingSliderTop = 48.dp
val paddingLevelInfoTop = 64.dp
val paddingLevelInfoBottom = 16.dp
val styleLightLevel = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold)

fun lightLevelInfo(lightLevel: Float): String {
  return when {
    lightLevel < 10 -> "非常に暗い (夜間)"
    lightLevel < 50 -> "暗い (屋内照明)"
    lightLevel < 200 -> "やや暗い (曇りの日)"
    lightLevel < 400 -> "普通 (室内)"
    lightLevel < 1000 -> "明るい (曇りの屋外)"
    lightLevel < 10000 -> "非常に明るい (晴れた日)"
    else -> "極めて明るい (直射日光)"
  }
}

@Composable
fun StyleTitle(): TextStyle {
  return MaterialTheme.typography.headlineMedium
}
