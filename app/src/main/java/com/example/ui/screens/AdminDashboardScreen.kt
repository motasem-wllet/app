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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentMethod
import com.example.model.Transaction
import com.example.model.UserProfile
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    allUsers: List<UserProfile>,
    transactions: List<Transaction>,
    paymentMethods: List<PaymentMethod>,
    onApproveTransaction: (String, String) -> Unit,
    onRejectTransaction: (String, String) -> Unit,
    onAdjustBalance: (String, Double, String) -> Boolean,
    onSavePaymentMethod: (PaymentMethod) -> Unit,
    onBack: () -> Unit
) {
    var adminTab by remember { mutableIntStateOf(0) }
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    val pendingTxns = transactions.filter { it.status == "pending" }
    val approvedTxns = transactions.filter { it.status == "approved" }
    val totalPlatformProfits = approvedTxns.sumOf { it.feeAmount }
    val totalAvailableBalances = allUsers.sumOf { it.availableBalance }
    val totalHeldBalances = allUsers.sumOf { it.heldBalance }

    // Dialog state for approval/rejection
    var selectedTxnForAction by remember { mutableStateOf<Transaction?>(null) }
    var actionNoteInput by remember { mutableStateOf("") }
    var actionDialogType by remember { mutableStateOf("") }

    // Dialog state for manual balance adjustment
    var selectedUserForAdjust by remember { mutableStateOf<UserProfile?>(null) }
    var adjustAmountInput by remember { mutableStateOf("") }
    var adjustReasonInput by remember { mutableStateOf("") }

    if (selectedTxnForAction != null) {
        AlertDialog(
            onDismissRequest = { selectedTxnForAction = null },
            title = {
                Text(
                    text = if (actionDialogType == "approve") "اعتماد المعاملة المالية رسمياً" else "رفض المعاملة المالية وإرجاع الرصيد",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("رقم المرجع: ${selectedTxnForAction!!.referenceNumber}", color = TextSecondary, fontSize = 12.sp)
                    Text("المبلغ: $${selectedTxnForAction!!.netAmount} ${selectedTxnForAction!!.currency}", color = NeonEmerald, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = actionNoteInput,
                        onValueChange = { actionNoteInput = it },
                        label = { Text(if (actionDialogType == "approve") "ملاحظة الاعتماد (اختياري)" else "سبب الرفض (إلزامي)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonEmerald, unfocusedBorderColor = ObsidianBorder)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val txn = selectedTxnForAction!!
                        val note = actionNoteInput.ifBlank { if (actionDialogType == "approve") "تم الاعتماد من الإدارة" else "مرفوض من الإدارة" }
                        if (actionDialogType == "approve") {
                            onApproveTransaction(txn.id, note)
                        } else {
                            onRejectTransaction(txn.id, note)
                        }
                        selectedTxnForAction = null
                        actionNoteInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (actionDialogType == "approve") NeonEmerald else CrimsonRed,
                        contentColor = Color.Black
                    )
                ) {
                    Text("تأكيد التنفيذ", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTxnForAction = null }) {
                    Text("إلغاء", color = TextSecondary)
                }
            },
            containerColor = ObsidianSurface
        )
    }

    if (selectedUserForAdjust != null) {
        AlertDialog(
            onDismissRequest = { selectedUserForAdjust = null },
            title = { Text("تعديل رصيد المستخدم في السجل", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("المستخدم: ${selectedUserForAdjust!!.name}", color = TextPrimary)
                    Text("الرصيد الحالي: $${selectedUserForAdjust!!.availableBalance} USD", color = CyberCyan, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = adjustAmountInput,
                        onValueChange = { adjustAmountInput = it },
                        label = { Text("المبلغ للإضافة (+) أو الخصم (-)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = ObsidianBorder)
                    )
                    OutlinedTextField(
                        value = adjustReasonInput,
                        onValueChange = { adjustReasonInput = it },
                        label = { Text("السبب الموثق في سجل العمليات الرقابي") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = ObsidianBorder)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val delta = adjustAmountInput.toDoubleOrNull() ?: 0.0
                        onAdjustBalance(selectedUserForAdjust!!.id, delta, adjustReasonInput)
                        selectedUserForAdjust = null
                        adjustAmountInput = ""
                        adjustReasonInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black)
                ) {
                    Text("حفظ التعديل", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForAdjust = null }) {
                    Text("إلغاء", color = TextSecondary)
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
                    text = "غرفة العمليات المركزية (Back-Office)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        // Admin Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = adminTab,
                containerColor = ObsidianCard,
                contentColor = NeonEmerald,
                edgePadding = 0.dp,
                indicator = {}
            ) {
                listOf(
                    "الخزينة والأرباح",
                    "مركز الطلبات (${pendingTxns.size})",
                    "بوابات الدفع",
                    "المستخدمين"
                ).forEachIndexed { idx, title ->
                    val isSelected = adminTab == idx
                    Tab(
                        selected = isSelected,
                        onClick = { adminTab = idx },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonEmerald else TextMuted
                            )
                        }
                    )
                }
            }
        }

        // TAB 0: Treasury & Analytics
        if (adminTab == 0) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TreasuryMetricCard(
                            title = "صافي أرباح العمولات",
                            amount = "$${numberFormat.format(totalPlatformProfits)}",
                            modifier = Modifier.weight(1f),
                            color = NeonEmerald
                        )
                        TreasuryMetricCard(
                            title = "الطلبات المعلقة",
                            amount = "${pendingTxns.size} طلب",
                            modifier = Modifier.weight(1f),
                            color = GoldAccent
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TreasuryMetricCard(
                            title = "إجمالي ودائع العملاء",
                            amount = "$${numberFormat.format(totalAvailableBalances)}",
                            modifier = Modifier.weight(1f),
                            color = CyberCyan
                        )
                        TreasuryMetricCard(
                            title = "الأرصدة المعلقة (Escrow)",
                            amount = "$${numberFormat.format(totalHeldBalances)}",
                            modifier = Modifier.weight(1f),
                            color = RoyalIndigo
                        )
                    }
                }
            }
        }

        // TAB 1: Operations & Approval Center
        else if (adminTab == 1) {
            item {
                Text(
                    text = "طلبات الإيداع والسحب المعلقة (${pendingTxns.size}):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            if (pendingTxns.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ObsidianCard,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("تمت معالجة كافة الطلبات المالية بنجاح!", color = TextSecondary)
                        }
                    }
                }
            } else {
                items(pendingTxns) { txn ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = ObsidianCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "مرجع: ${txn.referenceNumber}", fontWeight = FontWeight.Bold, color = TextPrimary)
                                Surface(shape = RoundedCornerShape(8.dp), color = GoldAccent.copy(alpha = 0.15f)) {
                                    Text(text = "قيد المراجعة", color = GoldAccent, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Text(text = "المبلغ الإجمالي: $${txn.grossAmount} ${txn.currency}", color = TextSecondary)
                            Text(text = "العمولة المحصلة: $${txn.feeAmount} | الصافي للمحفظة: $${txn.netAmount}", fontWeight = FontWeight.Bold, color = NeonEmerald)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        selectedTxnForAction = txn
                                        actionDialogType = "reject"
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonRed),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed)
                                ) {
                                    Text("رفض")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        selectedTxnForAction = txn
                                        actionDialogType = "approve"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black)
                                ) {
                                    Text("اعتماد فوري", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: Payment Methods Builder
        else if (adminTab == 2) {
            item {
                Text("بوابات وطرق الدفع النشطة:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            items(paymentMethods) { pm ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = ObsidianCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = pm.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                            Text(text = pm.currency, color = NeonEmerald, fontWeight = FontWeight.Bold)
                        }
                        Text(text = "الفئة: ${pm.category} | عمولة الإيداع: ${pm.depositFeePercent}% | عمولة السحب: ${pm.withdrawFeePercent}%", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // TAB 3: User Management
        else if (adminTab == 3) {
            item {
                Text("إدارة حسابات وأرصدة المستخدمين:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            items(allUsers) { usr ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = ObsidianCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = usr.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "${usr.walletNumber} • ${usr.email}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(text = "الرصيد: $${usr.availableBalance} USD", style = MaterialTheme.typography.labelSmall, color = NeonEmerald, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { selectedUserForAdjust = usr },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("تعديل الرصيد", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TreasuryMetricCard(title: String, amount: String, modifier: Modifier = Modifier, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = amount, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = color)
        }
    }
}
