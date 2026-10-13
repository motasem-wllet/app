package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ProcessingTransactionDialog(
    title: String = "جاري تنفيذ العملية المالية",
    subtext: String = "يتم التحقق الآمن من التوقيع الرقمي ومطابقة الرصيد",
    onComplete: () -> Unit
) {
    var stage by remember { mutableIntStateOf(0) } // 0: Encrypting, 1: Verifying, 2: Completed

    // Infinite rotation for spinning ring
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        delay(700)
        stage = 1
        delay(900)
        stage = 2
        delay(800)
        onComplete()
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ObsidianSurface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ObsidianBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Animated Dynamic Glowing Centerpiece
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(pulseScale),
                    contentAlignment = Alignment.Center
                ) {
                    if (stage < 2) {
                        // Outer neon rotating gradient ring
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(rotation)
                                .border(
                                    width = 3.dp,
                                    brush = Brush.sweepGradient(
                                        listOf(
                                            NeonEmerald,
                                            CyberCyan,
                                            RoyalIndigo,
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )
                    }

                    // Inner Glow Sphere
                    Surface(
                        modifier = Modifier.size(68.dp),
                        shape = CircleShape,
                        color = when (stage) {
                            2 -> NeonEmerald.copy(alpha = 0.2f)
                            1 -> CyberCyan.copy(alpha = 0.15f)
                            else -> RoyalIndigo.copy(alpha = 0.2f)
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            AnimatedContent(
                                targetState = stage,
                                transitionSpec = {
                                    fadeIn(tween(300)) + scaleIn() togetherWith fadeOut(tween(200)) + scaleOut()
                                },
                                label = "icon_transition"
                            ) { currentStage ->
                                when (currentStage) {
                                    0 -> Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = RoyalIndigo,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    1 -> Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = null,
                                        tint = CyberCyan,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    else -> Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = NeonEmerald,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Stage Title with Fade Transition
                AnimatedContent(
                    targetState = stage,
                    label = "text_stage"
                ) { currentStage ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (currentStage) {
                                0 -> "تشفير القناة المالية وحماية البيانات..."
                                1 -> "التحقق من الرصيد والبروتوكول الذكي..."
                                else -> "اكتملت المعاملة بنجاح!"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (currentStage == 2) NeonEmerald else TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (currentStage) {
                                0 -> "256-Bit TLS End-to-End Encryption"
                                1 -> "Matching Ledger & Holding Balance"
                                else -> "Ledger State Synced"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Step Progression Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StageDot(active = true, completed = stage >= 1, label = "تشفير")
                    Spacer(modifier = Modifier.width(8.dp))
                    StageLine(active = stage >= 1)
                    Spacer(modifier = Modifier.width(8.dp))
                    StageDot(active = stage >= 1, completed = stage >= 2, label = "تحقق")
                    Spacer(modifier = Modifier.width(8.dp))
                    StageLine(active = stage >= 2)
                    Spacer(modifier = Modifier.width(8.dp))
                    StageDot(active = stage >= 2, completed = stage == 2, label = "اكتمال")
                }
            }
        }
    }
}

@Composable
private fun StageDot(active: Boolean, completed: Boolean, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(
                    color = if (completed) NeonEmerald else if (active) CyberCyan else ObsidianBorder,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (completed) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.White, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = if (active) TextPrimary else TextMuted, fontSize = 10.sp)
    }
}

@Composable
private fun StageLine(active: Boolean) {
    Box(
        modifier = Modifier
            .width(28.dp)
            .height(2.dp)
            .background(if (active) NeonEmerald else ObsidianBorder)
    )
}
