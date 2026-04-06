package com.example.banglaclock

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.banglaclock.ui.theme.BanglaClockTheme
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BanglaClockTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BanglaDigitalClock(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BanglaDigitalClock(modifier: Modifier = Modifier) {
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(1_000 - (nowMillis % 1_000))
        }
    }

    val calendar = remember(nowMillis) {
        Calendar.getInstance().apply { timeInMillis = nowMillis }
    }

    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0B1026),
                    Color(0xFF1B2A64),
                    Color(0xFF3B2E78)
                )
            )
        )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width * 0.2f, size.height * 0.2f)
            drawCircle(
                color = Color(0xFF80DEEA).copy(alpha = 0.14f),
                radius = size.minDimension * 0.22f,
                center = center
            )
            drawCircle(
                color = Color(0xFFFFCC80).copy(alpha = 0.12f),
                radius = size.minDimension * 0.27f,
                center = Offset(size.width * 0.85f, size.height * 0.85f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                AnalogClock(
                    calendar = calendar,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(260.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = Color(0x33FFFFFF),
                shape = MaterialTheme.shapes.large,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    text = formatBanglaTime(nowMillis),
                    fontSize = 38.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF7F9FF),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BanglaDigitalClockPreview() {
    BanglaClockTheme {
        Box(
            modifier = Modifier
                .size(width = 380.dp, height = 640.dp)
        ) {
            BanglaDigitalClock(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun AnalogClock(calendar: Calendar, modifier: Modifier = Modifier) {
    val handColor = Color(0xFFEAF1FF)
    val accent = Color(0xFFFFB74D)

    Canvas(modifier = modifier.fillMaxWidth()) {
        val radius = size.minDimension / 2f
        val center = Offset(x = size.width / 2f, y = size.height / 2f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1B2F66),
                    Color(0xFF0E1A3D)
                ),
                center = center,
                radius = radius
            ),
            radius = radius * 0.97f,
            center = center
        )

        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFF80DEEA),
                    Color(0xFFFFE082),
                    Color(0xFFCE93D8),
                    Color(0xFF80DEEA)
                ),
                center = center
            ),
            radius = radius * 0.96f,
            center = center,
            style = Stroke(width = radius * 0.04f)
        )

        for (i in 0 until 60) {
            val angleRad = Math.toRadians((i * 6.0) - 90.0)
            val startRatio = if (i % 5 == 0) 0.75f else 0.84f
            val strokeWidth = if (i % 5 == 0) radius * 0.02f else radius * 0.01f

            val start = Offset(
                x = center.x + (radius * startRatio) * cos(angleRad).toFloat(),
                y = center.y + (radius * startRatio) * sin(angleRad).toFloat()
            )
            val end = Offset(
                x = center.x + (radius * 0.93f) * cos(angleRad).toFloat(),
                y = center.y + (radius * 0.93f) * sin(angleRad).toFloat()
            )

            drawLine(
                color = if (i % 5 == 0) Color(0xFFF8FBFF) else Color(0xFFCFD8DC),
                start = start,
                end = end,
                strokeWidth = if (i % 5 == 0) strokeWidth * 1.2f else strokeWidth,
                cap = StrokeCap.Round
            )
        }

        val textPaint = Paint().apply {
            color = Color(0xFFF7F9FF).toArgb()
            textSize = radius * 0.16f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        for (hour in 1..12) {
            val label = toBengaliDigits(hour.toString())
            val angleRad = Math.toRadians((hour * 30.0) - 90.0)
            val labelRadius = radius * 0.66f
            val labelX = center.x + labelRadius * cos(angleRad).toFloat()
            val labelY = center.y + labelRadius * sin(angleRad).toFloat() - ((textPaint.ascent() + textPaint.descent()) / 2f)

            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(label, labelX, labelY, textPaint)
            }
        }

        val seconds = calendar.get(Calendar.SECOND).toFloat()
        val minutes = calendar.get(Calendar.MINUTE) + (seconds / 60f)
        val hours = (calendar.get(Calendar.HOUR).toFloat()) + (minutes / 60f)

        drawHand(
            center = center,
            angleDegrees = hours * 30f,
            length = radius * 0.5f,
            strokeWidth = radius * 0.045f,
            color = handColor
        )
        drawHand(
            center = center,
            angleDegrees = minutes * 6f,
            length = radius * 0.72f,
            strokeWidth = radius * 0.03f,
            color = handColor
        )
        drawHand(
            center = center,
            angleDegrees = seconds * 6f,
            length = radius * 0.78f,
            strokeWidth = radius * 0.015f,
            color = accent
        )

        drawCircle(color = Color(0xFFF7F9FF), radius = radius * 0.05f, center = center)
        drawCircle(color = accent, radius = radius * 0.028f, center = center)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHand(
    center: Offset,
    angleDegrees: Float,
    length: Float,
    strokeWidth: Float,
    color: Color
) {
    val angleRad = Math.toRadians((angleDegrees - 90f).toDouble())
    val end = Offset(
        x = center.x + length * cos(angleRad).toFloat(),
        y = center.y + length * sin(angleRad).toFloat()
    )

    drawLine(
        color = color,
        start = center,
        end = end,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

private fun formatBanglaTime(timeMillis: Long): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = timeMillis }
    val hour12 = calendar.get(Calendar.HOUR).let { if (it == 0) 12 else it }
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val meridiem = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "পূর্বাহ্ণ" else "অপরাহ্ণ"

    val latinTime = String.format(Locale.US, "%02d:%02d:%02d", hour12, minute, second)
    return "$meridiem ${toBengaliDigits(latinTime)}"
}

private fun toBengaliDigits(value: String): String {
    val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    return buildString(value.length) {
        for (ch in value) {
            if (ch in '0'..'9') {
                append(bengaliDigits[ch - '0'])
            } else {
                append(ch)
            }
        }
    }
}