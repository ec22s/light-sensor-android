package com.ec22s.lightsensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
        Box(Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Main()
        }
      }
    }
  }
}

@Composable
fun Main() {
  var lightLevel by remember { mutableFloatStateOf(0f) }
  val isSensorActive = remember { mutableStateOf(false) }
  val intervalSec = remember { mutableIntStateOf(1) }

  LightSensorHandler(isSensorActive, intervalSec) {
    lightLevel = it
  }
  Column(Modifier
      .fillMaxWidth()
      .requiredWidth(mainSize)
      .padding(bottom = paddingMainBottom),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    TitleAndSwitch(isSensorActive)
    Column(Modifier.alpha(if (isSensorActive.value) 1f else 0f)) {
      SensorInfo(lightLevel)
      IntervalSlider(intervalSec)
    }
  }
}

@Composable
fun TitleAndSwitch(
  isSensorActive: MutableState<Boolean>,
) {
  Row(Modifier.padding(
      top = paddingSwitchTop, bottom = paddingSensorInfoTop
    ),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = title,
      style = StyleTitle(),
    )
    Switch(
      checked = isSensorActive.value,
      onCheckedChange = { isSensorActive.value = it },
      colors = SwitchDefaults.colors(),
      modifier = Modifier.padding(start = paddingSwitchLeft),
    )
  }
}

@Composable
fun LightSensorHandler(
  isSensorActive: MutableState<Boolean>,
  intervalSec: MutableState<Int>,
  onLightLevelChanged: (Float) -> Unit,
) {
  val context = LocalContext.current
  DisposableEffect(isSensorActive.value, intervalSec.value) {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    val listener = object : SensorEventListener {
      private var lastUpdateTime = 0L
      override fun onSensorChanged(event: SensorEvent?) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUpdateTime >= intervalSec.value * 1000) {
          if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            onLightLevelChanged(event.values[0])
            lastUpdateTime = currentTime
          }
        }
      }
      override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
    if (isSensorActive.value) {
      sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
    onDispose {
      sensorManager.unregisterListener(listener)
    }
  }
}

@Composable
fun SensorInfo(
  lightLevel: Float
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .fillMaxWidth()
      .height(mainSize)
  ) {
    EnergyGauge(lightLevel)
    Column(
      modifier = Modifier.fillMaxHeight(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Bottom
    ) {
      Text(
        text = String.format(Locale.getDefault(), formatLightLevel, lightLevel),
        style = styleLightLevel,
      )
      Text(
        text = unitLightLevel,
        style = MaterialTheme.typography.bodyLarge
      )
      Text(
        text = lightLevelInfo(lightLevel),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(
          top = paddingLevelInfoTop, bottom = paddingLevelInfoBottom),
      )
    }
  }
}

@Composable
fun EnergyGauge(lightLevel: Float) {
  val color = MaterialTheme.colorScheme
  val animatedLightLevel by animateFloatAsState(
    targetValue = lightLevel,
    animationSpec = tween(
      durationMillis = energyGaugeDurationMSec, easing = FastOutSlowInEasing
    )
  )
  Canvas(modifier = Modifier.fillMaxSize()) {
    val width = size.width
    val center = Offset(width / 2, size.height / 2)
    val radius = (width / 2)
    val circleSize = Size(radius * 2, radius * 2)
    val offset = Offset(center.x - radius, center.y - radius)
    val stroke = Stroke(width = energyGaugeWidth, cap = StrokeCap.Round)
    fun tmpDrawArc(valueRate: Float, color: Color) {
      drawArc(
        color = color,
        startAngle = 90 + (energyGaugeCutRate * 180),
        sweepAngle = 360 * (1 - energyGaugeCutRate) * valueRate,
        useCenter = false,
        topLeft = offset,
        size = circleSize,
        style = stroke
      )
    }
    tmpDrawArc(1f, color.primaryContainer)
    tmpDrawArc((animatedLightLevel / 1000f).coerceAtMost(1f), color.primary)
  }
}

@Composable
fun IntervalSlider(
  intervalSec: MutableState<Int>
) {
  var intervalSecDisplay by remember { mutableIntStateOf(1) }
  var sliderPosition by remember { mutableFloatStateOf(0f) }
  Column(
    modifier = Modifier.padding(top = paddingSliderTop),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = "$labelUpdate  $intervalSecDisplay $unitUpdate",
      style = MaterialTheme.typography.bodyLarge
    )
    Slider(
      value = sliderPosition,
      onValueChange = {
        sliderPosition = it
        intervalSecDisplay = ((sliderMax - 1) * sliderPosition).toInt() + 1
      },
      onValueChangeFinished = {
        intervalSec.value = intervalSecDisplay
      },
      colors = SliderDefaults.colors(
        activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
        // thumbColor = MaterialTheme.colorScheme.primaryContainer,
      )
    )
  }
}
