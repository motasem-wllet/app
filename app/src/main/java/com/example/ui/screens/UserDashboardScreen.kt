package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Transaction
import com.example.model.UserProfile
import com.example.ui.components.AnimatedFinancialChart
import com.example.ui.components.CurrencyExchangeWidget
import com.example.ui.components.FintechVirtualCard
import com.example.ui.theme.*

@Composable
fun UserDashboardScreen(
    currentUser: UserProfile,
    transactions: List<Transaction>,
    onNavigateDeposit: () -> Unit,
    onNavigateWithdraw: () -> Unit,
    onNavigateP2P: () -> Unit,
    onNavigateRequest: () -> Unit,
    onViewReceipt: (Transaction) -> Unit
) {
    var showQrDialog by remember { mutableStateOf(false) }
    val userTransactions = transactions.filter { it.userId == currentUser.id }

    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.QrCode2, contentDescription = null, tint = NeonEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("رمز QR لاستلام الأموال", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(2.dp, NeonEmerald, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentUser.walletNumber,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "شارك هذا الكود أو رقم المحفظة لاستلام التحويلات الفورية بأمان تام داخل شبكة نيكسوس العالمية.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showQrDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black)
                ) {
                    Text("إغلاق", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = ObsidianSurface
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // User Status & KYC Pill
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = RoyalIndigo.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalIndigo)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = currentUser.name.take(1),
                                fontWeight = FontWeight.Bold,
                                color = NeonEmerald,
                                fontSize = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "أهلاً بك،",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = currentUser.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (currentUser.kycStatus == "verified") NeonEmerald.copy(alpha = 0.15f) else GoldAccent.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (currentUser.kycStatus == "verified") NeonEmerald.copy(alpha = 0.5f) else GoldAccent.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (currentUser.kycStatus == "verified") Icons.Default.Verified else Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (currentUser.kycStatus == "verified") NeonEmerald else GoldAccent
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentUser.kycStatus == "verified") "حساب موثق KYC" else "قيد التوثيق",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (currentUser.kycStatus == "verified") NeonEmerald else GoldAccent,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Virtual FinTech Card
        item {
            FintechVirtualCard(
                user = currentUser,
                onShowQr = { showQrDialog = true }
            )
        }

        // Quick Action Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ModernQuickActionButton(
                    icon = Icons.Default.AddCard,
                    label = "إيداع رصيد",
                    accentColor = NeonEmerald,
                    onClick = onNavigateDeposit
                )
                ModernQuickActionButton(
                    icon = Icons.Default.ArrowOutward,
                    label = "إرسال / سحب",
                    accentColor = CyberCyan,
                    onClick = onNavigateWithdraw
                )
                ModernQuickActionButton(
                    icon = Icons.Default.SwapHoriz,
                    label = "تحويل P2P",
                    accentColor = RoyalIndigo,
                    onClick = onNavigateP2P
                )
                ModernQuickActionButton(
                    icon = Icons.Default.RequestQuote,
                    label = "طلب أموال",
                    accentColor = GoldAccent,
                    onClick = onNavigateRequest
                )
            }
        }

        // Live Financial Chart
        item {
            AnimatedFinancialChart()
        }

        // Live FX Currency Converter
        item {
            CurrencyExchangeWidget()
        }

        // Recent Activity Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "النشاط المالي الأخير",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = onNavigateDeposit) {
                    Text("عرض الكل", color = NeonEmerald, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (userTransactions.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ObsidianCard,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا توجد معاملات مسجلة بعد",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            items(userTransactions.take(5)) { txn ->
                PremiumTransactionCard(txn = txn, onClick = { onViewReceipt(txn) })
            }
        }
    }
}

@Composable
fun ModernQuickActionButton(
    icon: ImageVector,
    label: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(20.dp),
            color = ObsidianCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
            modifier = Modifier.size(58.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            fontSize = 11.sp
        )
    }
}

@Composable
fun PremiumTransactionCard(txn: Transaction, onClick: () -> Unit) {
    val isIncome = txn.type == "deposit" || txn.type == "p2p_receive"
    val statusColor = when (txn.status) {
        "approved" -> NeonEmerald
        "pending" -> GoldAccent
        else -> CrimsonRed
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = statusColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when (txn.type) {
                                "deposit" -> Icons.Default.ArrowDownward
                                "withdraw" -> Icons.Default.ArrowUpward
                                else -> Icons.Default.SwapHoriz
                            },
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = when (txn.type) {
                            "deposit" -> "إيداع رصيد فوري"
                            "withdraw" -> "سحب خارجي"
                            "p2p_send" -> "تحويل داخلي صادر"
                            "p2p_receive" -> "تحويل داخلي وارد"
                            else -> "معاملة دفع"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${txn.referenceNumber} • ${if (txn.status == "approved") "ناجحة" else if (txn.status == "pending") "قيد المراجعة" else "مرفوضة"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isIncome) "+" else "-"}$${txn.netAmount} ${txn.currency}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isIncome) NeonEmerald else TextPrimary
                )
                Text(
                    text = "الرسوم: $${txn.feeAmount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
