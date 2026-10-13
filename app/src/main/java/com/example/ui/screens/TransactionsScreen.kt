package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Transaction
import com.example.model.UserProfile
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionsScreen(
    currentUser: UserProfile,
    transactions: List<Transaction>,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }
    var viewingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showReceiptDialog by remember { mutableStateOf(false) }

    val userTxns = transactions.filter { it.userId == currentUser.id }
    val filteredTxns = userTxns.filter { txn ->
        val matchesSearch = txn.referenceNumber.contains(searchQuery, ignoreCase = true) || txn.type.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "deposit" -> txn.type == "deposit"
            "withdraw" -> txn.type == "withdraw"
            "p2p" -> txn.type.contains("p2p")
            else -> true
        }
        matchesSearch && matchesFilter
    }

    // High-Fidelity Printable Digital Receipt Dialog
    if (showReceiptDialog && viewingTransaction != null) {
        val txn = viewingTransaction!!
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateStr = dateFormat.format(Date(txn.createdAt))

        AlertDialog(
            onDismissRequest = { showReceiptDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = NeonEmerald.copy(alpha = 0.15f), modifier = Modifier.size(36.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("إيصال مالي رسمي معتمد", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ObsidianCard, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header Brand
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("NEXUS FINTECH GLOBAL", fontWeight = FontWeight.ExtraBold, color = NeonEmerald, fontSize = 11.sp)
                        Text("VERIFIED RECEIPT", fontWeight = FontWeight.Bold, color = CyberCyan, fontSize = 11.sp)
                    }
                    Divider(color = ObsidianBorder)

                    ReceiptLine("رقم المعاملة (Ref):", txn.referenceNumber, isMono = true)
                    ReceiptLine("النوع:", when(txn.type) {
                        "deposit" -> "إيداع رصيد فوري"
                        "withdraw" -> "سحب خارجي"
                        "p2p_send" -> "تحويل داخلي صادر"
                        "p2p_receive" -> "تحويل داخلي وارد"
                        else -> txn.type
                    })
                    ReceiptLine("الحالة الرسمية:", if (txn.status == "approved") "معتمد ومسجل في الشبكة" else if (txn.status == "pending") "قيد المراجعة" else "مرفوضة")
                    ReceiptLine("المبلغ الإجمالي (Gross):", "$${txn.grossAmount} ${txn.currency}")
                    ReceiptLine("عمولة المنصة والشبكة:", "-$${txn.feeAmount} ${txn.currency}", isRed = true)
                    Divider(color = ObsidianBorder)
                    ReceiptLine("المبلغ الصافي (Net):", "$${txn.netAmount} ${txn.currency}", isBold = true, isGreen = true)
                    ReceiptLine("الطابع الزمني:", dateStr)

                    if (txn.adminNote.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("ملاحظة التدقيق المالي:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted)
                        Text(txn.adminNote, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showReceiptDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حفظ / طباعة الإيصال", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReceiptDialog = false }) {
                    Text("إغلاق", color = TextSecondary)
                }
            },
            containerColor = ObsidianSurface
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(ObsidianCard, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "عودة", tint = TextPrimary)
                }
                Text(
                    text = "سجل الحساب المالي (Ledger)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("بحث برقم المرجع (TXN-xxxxxx)") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NeonEmerald) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonEmerald,
                    unfocusedBorderColor = ObsidianBorder
                )
            )
        }

        // Filters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("all" to "الكل", "deposit" to "الإيداعات", "withdraw" to "السحوبات", "p2p" to "التحويلات").forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    Surface(
                        onClick = { selectedFilter = key },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) NeonEmerald.copy(alpha = 0.15f) else ObsidianCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) NeonEmerald else ObsidianBorder
                        )
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            color = if (isSelected) NeonEmerald else TextMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        if (filteredTxns.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ObsidianCard,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("لا توجد نتائج مطابقة لبحثك", color = TextSecondary)
                    }
                }
            }
        } else {
            items(filteredTxns) { txn ->
                PremiumTransactionCard(
                    txn = txn,
                    onClick = {
                        viewingTransaction = txn
                        showReceiptDialog = true
                    }
                )
            }
        }
    }
}

@Composable
private fun ReceiptLine(
    label: String,
    value: String,
    isBold: Boolean = false,
    isGreen: Boolean = false,
    isRed: Boolean = false,
    isMono: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 12.sp)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default,
            color = if (isGreen) NeonEmerald else if (isRed) CrimsonRed else TextPrimary,
            fontSize = 12.sp
        )
    }
}
