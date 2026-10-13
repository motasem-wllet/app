package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FintechVirtualCard(
    user: UserProfile,
    onShowQr: () -> Unit
) {
    var showBalance by remember { mutableStateOf(true) }
    var copiedRecently by remember { mutableStateOf(false) }
    var isCardFrozen by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    // Shimmer highlight effect
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_pos"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp),
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = if (isCardFrozen) {
                            listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        } else {
                            listOf(
                                Color(0xFF0B192C),
                                Color(0xFF1E3E62),
                                Color(0xFF000000)
                            )
                        }
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            NeonEmerald.copy(alpha = 0.6f),
                            CyberCyan.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Brand & Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonEmerald.copy(alpha = 0.15f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "NEXUS PLATINUM",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp,
                                color = NeonEmerald
                            )
                            Text(
                                text = if (isCardFrozen) "المحفظة مجمدة مؤقتاً" else "Global Multi-Currency",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isCardFrozen) CrimsonRed else TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Eye Toggle for Balance
                        IconButton(
                            onClick = { showBalance = !showBalance },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.08f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (showBalance) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "إظهار/إخفاء",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // QR Code Button
                        IconButton(
                            onClick = onShowQr,
                            modifier = Modifier
                                .size(36.dp)
                                .background(NeonEmerald.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "QR Code",
                                tint = NeonEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Middle Section: Balance & Chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "الرصيد المتاح للاستخدام",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        AnimatedContent(
                            targetState = showBalance,
                            label = "balance_anim"
                        ) { isVisible ->
                            if (isVisible) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$${numberFormat.format(user.availableBalance)}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "USD",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonEmerald
                                    )
                                }
                            } else {
                                Text(
                                    text = "$ ••••••••",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        if (user.heldBalance > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(GoldAccent, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "رصيد معلق: $${numberFormat.format(user.heldBalance)} USD",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent
                                )
                            }
                        }
                    }

                    // Simulated Gold EMV Chip
                    Surface(
                        modifier = Modifier
                            .width(42.dp)
                            .height(30.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFD4AF37),
                        tonalElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(1.dp, Color(0xFFA17C10), RoundedCornerShape(6.dp))
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Contactless,
                                contentDescription = null,
                                tint = Color.Black.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.CenterEnd)
                            )
                        }
                    }
                }

                // Bottom Row: Wallet Number & Copy Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                clipboardManager.setText(AnnotatedString(user.walletNumber))
                                copiedRecently = true
                                coroutineScope.launch {
                                    delay(1500)
                                    copiedRecently = false
                                }
                            }
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = user.walletNumber,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (copiedRecently) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "نسخ",
                            tint = if (copiedRecently) NeonEmerald else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name.split(" ").firstOrNull() ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Dual Circle Card Badge (Mastercard style)
                        Box(modifier = Modifier.size(24.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(CrimsonRed.copy(alpha = 0.85f), CircleShape)
                                    .align(Alignment.CenterStart)
                            )
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(GoldAccent.copy(alpha = 0.85f), CircleShape)
                                    .align(Alignment.CenterEnd)
                            )
                        }
                    }
                }
            }
        }
    }
}
