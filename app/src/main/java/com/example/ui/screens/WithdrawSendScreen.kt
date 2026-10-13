package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentMethod
import com.example.model.UserProfile
import com.example.ui.components.ProcessingTransactionDialog
import com.example.ui.theme.*

@Composable
fun WithdrawSendScreen(
    currentUser: UserProfile,
    allUsers: List<UserProfile>,
    paymentMethods: List<PaymentMethod>,
    onWithdrawSubmitted: (String, Double, Boolean, Map<String, String>) -> Pair<Boolean, String>,
    onP2PTransfer: (String, Double, String) -> Pair<Boolean, String>,
    onCreatePaymentRequest: (Double, String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: P2P, 1: Withdraw, 2: Request

    // P2P State
    var p2pRecipient by remember { mutableStateOf("") }
    var p2pAmount by remember { mutableStateOf("") }
    var p2pNote by remember { mutableStateOf("") }

    // Withdraw State
    val withdrawMethods = paymentMethods.filter { it.allowWithdraw }
    var selectedWithdrawMethod by remember { mutableStateOf(withdrawMethods.firstOrNull()) }
    var withdrawAmount by remember { mutableStateOf("") }
    var feeBearerDeduct by remember { mutableStateOf(true) }
    var withdrawFields by remember { mutableStateOf(mapOf<String, String>()) }

    // Request Money State
    var reqAmount by remember { mutableStateOf("") }
    var reqNote by remember { mutableStateOf("") }

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var actionTypeToConfirm by remember { mutableStateOf("") }
    var isProcessingAnimation by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isErrorFeedback by remember { mutableStateOf(false) }

    if (isProcessingAnimation) {
        ProcessingTransactionDialog(
            title = if (actionTypeToConfirm == "p2p") "تنفيذ التحويل اللحظي (P2P)" else "معالجة طلب السحب الخارجي",
            subtext = "التحقق من الرصيد وتوقيع المعاملة بمحفظة المستلم",
            onComplete = {
                isProcessingAnimation = false
                if (actionTypeToConfirm == "p2p") {
                    val amt = p2pAmount.toDoubleOrNull() ?: 0.0
                    val (ok, msg) = onP2PTransfer(p2pRecipient, amt, p2pNote)
                    feedbackMessage = msg
                    isErrorFeedback = !ok
                    if (ok) {
                        p2pAmount = ""
                        p2pRecipient = ""
                        p2pNote = ""
                    }
                } else if (actionTypeToConfirm == "withdraw") {
                    val amt = withdrawAmount.toDoubleOrNull() ?: 0.0
                    val (ok, msg) = onWithdrawSubmitted(selectedWithdrawMethod!!.id, amt, feeBearerDeduct, withdrawFields)
                    feedbackMessage = msg
                    isErrorFeedback = !ok
                    if (ok) {
                        withdrawAmount = ""
                    }
                }
            }
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
                    text = "الإرسال والسحب الخارجي",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        // Luxury Tab Row
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = ObsidianCard,
                contentColor = NeonEmerald,
                indicator = {}
            ) {
                listOf("تحويل فوري (P2P)", "سحب خارجي", "طلب دفع").forEachIndexed { idx, title ->
                    val isSelected = selectedTab == idx
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = idx },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (isSelected) NeonEmerald else TextMuted
                            )
                        }
                    )
                }
            }
        }

        if (feedbackMessage != null) {
            item {
                Surface(
                    color = if (isErrorFeedback) CrimsonRed.copy(alpha = 0.15f) else NeonEmerald.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isErrorFeedback) CrimsonRed else NeonEmerald),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = feedbackMessage!!,
                        modifier = Modifier.padding(16.dp),
                        color = if (isErrorFeedback) CrimsonRed else NeonEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // TAB 0: P2P Instant Transfer
        if (selectedTab == 0) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = ObsidianCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = RoyalIndigo.copy(alpha = 0.2f), modifier = Modifier.size(36.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = RoyalIndigo)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("التحويل الداخلي الفوري (P2P)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("إرسال فوري ومباشر بدون انتظار برسم 0.5% فقط", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                        }

                        OutlinedTextField(
                            value = p2pRecipient,
                            onValueChange = { p2pRecipient = it },
                            label = { Text("رقم محفظة المستلم (مثل W-88371209)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonEmerald, unfocusedBorderColor = ObsidianBorder)
                        )

                        val foundRecipient = allUsers.find { it.walletNumber == p2pRecipient }
                        if (p2pRecipient.isNotBlank()) {
                            if (foundRecipient != null) {
                                Surface(
                                    color = NeonEmerald.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.3f))
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("تم التحقق من المستلم: ${foundRecipient.name}", fontWeight = FontWeight.Bold, color = NeonEmerald, fontSize = 12.sp)
                                    }
                                }
                            } else {
                                Text("رقم المحفظة غير مسجل في شبكة نيكسوس", color = CrimsonRed, style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        OutlinedTextField(
                            value = p2pAmount,
                            onValueChange = { p2pAmount = it },
                            label = { Text("المبلغ المطلوب تحويله (USD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonEmerald, unfocusedBorderColor = ObsidianBorder)
                        )

                        OutlinedTextField(
                            value = p2pNote,
                            onValueChange = { p2pNote = it },
                            label = { Text("ملاحظة أو سبب الحوالة (اختياري)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonEmerald, unfocusedBorderColor = ObsidianBorder)
                        )

                        Button(
                            onClick = {
                                if (foundRecipient == null) {
                                    feedbackMessage = "الرجاء التأكد من رقم محفظة المستلم أولاً."
                                    isErrorFeedback = true
                                    return@Button
                                }
                                val amt = p2pAmount.toDoubleOrNull() ?: 0.0
                                if (amt <= 0) {
                                    feedbackMessage = "الرجاء إدخال مبلغ تحويل صالح."
                                    isErrorFeedback = true
                                    return@Button
                                }
                                feedbackMessage = null
                                actionTypeToConfirm = "p2p"
                                showPinDialog = true
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تأكيد وإرسال الحوالة الفورية", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // TAB 1: External Withdrawal
        else if (selectedTab == 1) {
            item {
                Text(
                    text = "اختر وجهة السحب الخارجي:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            items(withdrawMethods) { method ->
                val isSelected = selectedWithdrawMethod?.id == method.id
                Surface(
                    onClick = { selectedWithdrawMethod = method },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) ObsidianCardHighlight else ObsidianCard,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) CyberCyan else ObsidianBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = method.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Surface(shape = RoundedCornerShape(8.dp), color = CyberCyan.copy(alpha = 0.15f)) {
                            Text(text = "رسوم: ${method.withdrawFeePercent}%", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = CyberCyan, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            selectedWithdrawMethod?.let { method ->
                item {
                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it },
                        label = { Text("المبلغ المراد سحبه (${method.currency})") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = ObsidianBorder)
                    )
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = ObsidianCardHighlight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("خيارات خصم العمولة (Fee Bearer):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { feeBearerDeduct = true },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("خصم العمولة من المبلغ المسحوب", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                RadioButton(selected = feeBearerDeduct, onClick = { feeBearerDeduct = true }, colors = RadioButtonDefaults.colors(selectedColor = CyberCyan))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { feeBearerDeduct = false },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("إضافة العمولة على المبلغ (يصل المبلغ صافياً)", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                RadioButton(selected = !feeBearerDeduct, onClick = { feeBearerDeduct = false }, colors = RadioButtonDefaults.colors(selectedColor = CyberCyan))
                            }
                        }
                    }
                }

                items(method.requiredFields) { fieldName ->
                    var fVal by remember(fieldName) { mutableStateOf(withdrawFields[fieldName] ?: "") }
                    OutlinedTextField(
                        value = fVal,
                        onValueChange = {
                            fVal = it
                            withdrawFields = withdrawFields + (fieldName to it)
                        },
                        label = { Text("بيانات الاستلام: $fieldName") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = ObsidianBorder)
                    )
                }

                item {
                    Button(
                        onClick = {
                            val amt = withdrawAmount.toDoubleOrNull() ?: 0.0
                            if (amt <= 0) {
                                feedbackMessage = "الرجاء إدخال مبلغ سحب صحيح."
                                isErrorFeedback = true
                                return@Button
                            }
                            feedbackMessage = null
                            actionTypeToConfirm = "withdraw"
                            showPinDialog = true
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black)
                    ) {
                        Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تقديم طلب السحب", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // TAB 2: Request Money / Invoice
        else if (selectedTab == 2) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = ObsidianCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("إنشاء رابط دفع / فاتورة مستحقة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("يمكنك إنشاء رابط دفع بمبلغ محدد لمشاركته مع العملاء ليتم السداد إلى محفظتك مباشرة.", style = MaterialTheme.typography.bodySmall, color = TextMuted)

                        OutlinedTextField(
                            value = reqAmount,
                            onValueChange = { reqAmount = it },
                            label = { Text("المبلغ المطلوب (USD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, unfocusedBorderColor = ObsidianBorder)
                        )

                        OutlinedTextField(
                            value = reqNote,
                            onValueChange = { reqNote = it },
                            label = { Text("الغرض / وصف الفاتورة") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, unfocusedBorderColor = ObsidianBorder)
                        )

                        Button(
                            onClick = {
                                val amt = reqAmount.toDoubleOrNull() ?: 0.0
                                if (amt <= 0) {
                                    feedbackMessage = "الرجاء إدخال مبلغ صالح."
                                    isErrorFeedback = true
                                    return@Button
                                }
                                onCreatePaymentRequest(amt, reqNote)
                                feedbackMessage = "تم إنشاء رابط الدفع والفاتورة بنجاح!"
                                isErrorFeedback = false
                                reqAmount = ""
                                reqNote = ""
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black)
                        ) {
                            Icon(imageVector = Icons.Default.Receipt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("توليد رابط الفاتورة", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeonEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تأكيد العملية برمز PIN", color = TextPrimary)
                }
            },
            text = {
                Column {
                    Text("أدخل رمز المعاملات لتأكيد الخصم المالي:", color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        label = { Text("Transaction PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput == currentUser.pinCode) {
                            showPinDialog = false
                            isProcessingAnimation = true
                        } else {
                            feedbackMessage = "رمز الـ PIN غير صحيح."
                            isErrorFeedback = true
                            showPinDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black)
                ) {
                    Text("تأكيد ومتابعة", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("إلغاء", color = TextSecondary)
                }
            },
            containerColor = ObsidianSurface
        )
    }
}
