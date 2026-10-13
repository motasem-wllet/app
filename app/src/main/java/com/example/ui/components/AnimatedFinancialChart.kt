package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AnimatedFinancialChart(
    modifier: Modifier = Modifier
) {
    // Animation progress for line draw
    val transition = rememberInfiniteTransition(label = "chart_glow")
    val glowAlpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    var animationTarget by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        animationTarget = 1f
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "التدفق المالي الأسبوعي",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "+24.8% مقارنة بالأسبوع الماضي",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonEmerald
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ObsidianSurface,
                    modifier = Modifier.border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(NeonEmerald, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مباشر",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Data points normalized [0.0..1.0]
                    val points = listOf(0.25f, 0.45f, 0.35f, 0.70f, 0.55f, 0.90f, 0.85f)
                    val stepX = width / (points.size - 1)

                    val linePath = Path()
                    val fillPath = Path()

                    points.forEachIndexed { index, normY ->
                        val currentProgress = (animatedProgress * points.size).coerceIn(0f, points.size.toFloat())
                        if (index <= currentProgress) {
                            val x = index * stepX
                            val targetY = height - (normY * (height - 20.dp.toPx())) - 10.dp.toPx()

                            if (index == 0) {
                                linePath.moveTo(x, targetY)
                                fillPath.moveTo(x, height)
                                fillPath.lineTo(x, targetY)
                            } else {
                                val prevX = (index - 1) * stepX
                                val prevY = height - (points[index - 1] * (height - 20.dp.toPx())) - 10.dp.toPx()
                                val cX1 = (prevX + x) / 2
                                val cY1 = prevY
                                val cX2 = (prevX + x) / 2
                                val cY2 = targetY
                                linePath.cubicTo(cX1, cY1, cX2, cY2, x, targetY)
                                fillPath.cubicTo(cX1, cY1, cX2, cY2, x, targetY)
                            }

                            if (index == points.size - 1) {
                                fillPath.lineTo(x, height)
                                fillPath.close()
                            }
                        }
                    }

                    // Fill Gradient
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                NeonEmerald.copy(alpha = 0.35f * glowAlpha),
                                Color.Transparent
                            )
                        )
                    )

                    // Line Stroke
                    drawPath(
                        path = linePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                CyberCyan,
                                NeonEmerald
                            )
                        ),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Latest active point indicator
                    if (points.isNotEmpty()) {
                        val lastIdx = points.size - 1
                        val lastX = lastIdx * stepX
                        val lastY = height - (points[lastIdx] * (height - 20.dp.toPx())) - 10.dp.toPx()
                        drawCircle(
                            color = NeonEmerald,
                            radius = 5.dp.toPx(),
                            center = Offset(lastX, lastY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.5.dp.toPx(),
                            center = Offset(lastX, lastY)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Day Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("السبت", "الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
