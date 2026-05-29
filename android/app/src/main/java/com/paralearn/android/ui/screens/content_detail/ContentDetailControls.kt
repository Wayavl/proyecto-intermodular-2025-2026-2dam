package com.paralearn.android.ui.screens.content_detail

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paralearn.android.ui.theme.PrimaryBlue
import com.paralearn.android.ui.theme.PrimaryCyan
import com.paralearn.android.ui.theme.SpaceGrotesk
import com.paralearn.android.ui.theme.appBackgroundColor
import com.paralearn.android.ui.theme.appSurfaceContainer
import com.paralearn.android.ui.theme.appSurfaceLowest
import com.paralearn.android.ui.theme.appTextMainColor
import com.paralearn.android.ui.theme.appTextSecondaryColor

@Composable
fun ContentGridVisualizer(isSimulating: Boolean, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "grid")
    val sweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(3000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "sweep"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(appSurfaceLowest().copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "KERNEL VISUALIZER",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = SpaceGrotesk,
            letterSpacing = 1.sp,
            color = appTextSecondaryColor()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF040E1F))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cols = 12
                val rows = 8
                val cellW = size.width / cols
                val cellH = size.height / rows
                val activeCol = if (isSimulating) (sweep * cols).toInt() else 5
                for (c in 0 until cols) {
                    for (r in 0 until rows) {
                        val color = when {
                            c == activeCol -> PrimaryCyan.copy(alpha = 0.9f)
                            c in 3..6 && r in 2..5 -> PrimaryBlue.copy(alpha = 0.6f)
                            else -> Color(0xFF1E293B).copy(alpha = 0.35f)
                        }
                        drawRect(
                            color = color,
                            topLeft = Offset(c * cellW + 2f, r * cellH + 2f),
                            size = Size(cellW - 4f, cellH - 4f)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(appBackgroundColor().copy(alpha = 0.85f))
                    .border(1.dp, PrimaryCyan.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isSimulating) "STATUS: CALCULATING..." else "STATUS: IDLE",
                    color = PrimaryCyan,
                    fontSize = 10.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun ContentInteractiveControlsCard(
    controls: List<ControlConfig>,
    values: Map<String, Any>,
    onValueChange: (String, Any) -> Unit,
    onButtonClick: (String, Map<String, Any>) -> Unit,
    isSimulating: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(appSurfaceContainer().copy(alpha = 0.9f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
            Text(
                text = "INTERACTIVE CONTROLS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = SpaceGrotesk,
                letterSpacing = 1.sp,
                color = appTextMainColor()
            )
        }

        // Separate controls: display input controls first, then buttons
        val inputControls = controls.filter { it.type != "button" }
        val actionButtons = controls.filter { it.type == "button" }

        inputControls.forEach { ctrl ->
            when (ctrl.type) {
                "slider" -> {
                    val currentVal = (values[ctrl.id] as? Double) ?: (ctrl.defaultValue as? Double) ?: 512.0
                    val min = ctrl.min ?: 128.0
                    val max = ctrl.max ?: 2048.0
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = ctrl.label, fontSize = 13.sp, color = appTextMainColor(), fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold)
                            Text(text = currentVal.toInt().toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        }
                        Slider(
                            value = currentVal.toFloat(),
                            onValueChange = { onValueChange(ctrl.id, it.toDouble()) },
                            valueRange = min.toFloat()..max.toFloat(),
                            colors = SliderDefaults.colors(thumbColor = PrimaryCyan, activeTrackColor = PrimaryCyan, inactiveTrackColor = Color(0xFF1E293B))
                        )
                    }
                }
                "select" -> {
                    val currentVal = (values[ctrl.id] as? String) ?: (ctrl.defaultValue as? String).orEmpty()
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = ctrl.label, fontSize = 13.sp, color = appTextMainColor(), fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ctrl.options?.forEach { option ->
                                val selected = currentVal == option
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) PrimaryBlue else Color(0xFF111C2D))
                                        .border(
                                            1.dp,
                                            if (selected) PrimaryCyan else Color.White.copy(alpha = 0.08f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { onValueChange(ctrl.id, option) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = option, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
                "toggle" -> {
                    val currentVal = (values[ctrl.id] as? Boolean) ?: (ctrl.defaultValue as? Boolean) ?: false
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = ctrl.label, fontSize = 13.sp, color = appTextMainColor(), fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold)
                            Text(text = "Runtime parameter", fontSize = 10.sp, color = appTextSecondaryColor())
                        }
                        Switch(
                            checked = currentVal,
                            onCheckedChange = { onValueChange(ctrl.id, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryCyan, checkedTrackColor = PrimaryBlue)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (actionButtons.isNotEmpty()) {
            actionButtons.forEach { btn ->
                Button(
                    onClick = { onButtonClick(btn.id, btn.params ?: emptyMap()) },
                    enabled = !isSimulating,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (isSimulating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Text(
                                btn.label.uppercase(),
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        } else {
            // Fallback button if none defined in controls JSON
            Button(
                onClick = { onButtonClick("run_default", emptyMap()) },
                enabled = !isSimulating,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isSimulating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Text(
                            "RUN BENCHMARK",
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContentTelemetryCard(telemetry: TelemetryState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(appSurfaceContainer().copy(alpha = 0.7f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("REAL-TIME TELEMETRY", fontSize = 11.sp, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, color = appTextSecondaryColor(), letterSpacing = 1.sp)
            Box(modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(PrimaryCyan))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("VRAM LOAD", fontSize = 10.sp, color = appTextSecondaryColor(), fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
            Text(String.format("%.0f%%", telemetry.vramUsage * 100f), fontSize = 10.sp, color = PrimaryCyan, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0xFF040E1F))) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(telemetry.vramUsage.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(3.dp))
                    .background(Brush.horizontalGradient(listOf(PrimaryBlue, PrimaryCyan)))
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LATENCY", fontSize = 10.sp, color = appTextSecondaryColor())
                Text(String.format("%.1fms", telemetry.latencyMs), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = appTextMainColor(), fontFamily = SpaceGrotesk)
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("THROUGHPUT", fontSize = 10.sp, color = appTextSecondaryColor())
                Text(String.format("%.0f GFLOPS", telemetry.throughputGflops), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = appTextMainColor(), fontFamily = SpaceGrotesk)
            }
        }
    }
}
