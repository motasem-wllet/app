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
import androidx.compose.ui.draw.clip
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
fun DepositScreen(
    currentUser: UserProfile,
    paymentMethods: List<PaymentMethod>,
    onDepositSubmitted: (String, Double, Map<String, String>, String) -> Boolean,
    onBack: () -> Unit
) {
    val depositMethods = paymentMethods.filter { it.allowDeposit }
    var selectedMethod by remember { mutableStateOf(depositMethods.firstOrNull()) }
    var amountInput by remember { mutableStateOf("") }
    var customFieldValues by remember { mutableStateOf(mapOf<String, String>()) }
    var pinInput by remember { mutableStateOf("") }
    var showPinDialog by remember { mutableStateOf(false) }
    var isProcessingAnimation by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val gross = amountInput.toDoubleOrNull() ?: 0.0
    val fee = selectedMethod?.let { (gross * (it.depositFeePercent / 100.0)) + it.depositFixedFee } ?: 0.0
    val net = maxOf(0.0, gross - fee)

    if (isProcessingAnimation) {
        ProcessingTransactionDialog(
            title = "جاري معالجة طلب الإيداع",
            subtext = "يتم التحقق من الحساب وتأمين إيداع الرصيد",
            onComplete = {
                isProcessingAnimation = false
                val success = onDepositSubmitted(selectedMethod!!.id, gross, customFieldValues, "")
                if (success) {
                    successMessage = "تم تقديم طلب الإيداع بنجاح وتأمينه في سجل العمليات!"
                    amountInput = ""
                } else {
                    errorMessage = "حدث خطأ أثناء معالجة الطلب."
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
                    text = "إيداع وتغذية الرصيد",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        if (successMessage != null) {
            item {
                Surface(
                    color = NeonEmerald.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = successMessage!!,
                        modifier = Modifier.padding(16.dp),
                        color = NeonEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (errorMessage != null) {
            item {
                Surface(
                    color = CrimsonRed.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        modifier = Modifier.padding(16.dp),
                        color = CrimsonRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Text(
                text = "اختر بوابة الدفع المعتمدة:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(depositMethods) { method ->
            val isSelected = selectedMethod?.id == method.id
            Surface(
                onClick = { selectedMethod = method },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) ObsidianCardHighlight else ObsidianCard,
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) NeonEmerald else ObsidianBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) NeonEmerald.copy(alpha = 0.2f) else ObsidianSurface,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = when (method.category) {
                                        "crypto" -> Icons.Default.CurrencyBitcoin
                                        "bank" -> Icons.Default.AccountBalance
                                        "local" -> Icons.Default.PhoneAndroid
                                        else -> Icons.Default.CreditCard
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) NeonEmerald else TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = method.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "الحد الأدنى: ${method.minAmount} ${method.currency}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NeonEmerald.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "عمولة: ${method.depositFeePercent}%",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        selectedMethod?.let { method ->
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ObsidianCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تعليمات التحويل الرسمية:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = method.instructions, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = ObsidianSurface,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "حساب الإيداع الخاص بالمنصة:\n${method.merchantAccountDetails}",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("المبلغ المراد إيداعه (${method.currency})") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonEmerald,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedLabelColor = NeonEmerald
                    )
                )
            }

            // Live Fee Breakdown
            if (gross > 0) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ObsidianCardHighlight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = CyberCyan)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("الحاسبة المالية اللحظية (Smart Fee Engine):", fontWeight = FontWeight.Bold, color = CyberCyan)
                            }
                            Divider(color = ObsidianBorder)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("المبلغ المحول:", color = TextSecondary)
                                Text("$gross ${method.currency}", fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("رسوم الشبكة (${method.depositFeePercent}%):", color = TextSecondary)
                                Text("-$fee ${method.currency}", color = CrimsonRed, fontWeight = FontWeight.Bold)
                            }
                            Divider(color = ObsidianBorder)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("المبلغ الصافي النهائي في المحفظة:", fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("$$net ${method.currency}", fontWeight = FontWeight.ExtraBold, color = NeonEmerald, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            items(method.requiredFields) { fieldName ->
                var fieldValue by remember(fieldName) { mutableStateOf(customFieldValues[fieldName] ?: "") }
                OutlinedTextField(
                    value = fieldValue,
                    onValueChange = {
                        fieldValue = it
                        customFieldValues = customFieldValues + (fieldName to it)
                    },
                    label = { Text("بيانات إضافية: $fieldName") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = ObsidianBorder
                    )
                )
            }

            item {
                Button(
                    onClick = {
                        if (gross < method.minAmount || gross > method.maxAmount) {
                            errorMessage = "المبلغ خارج الحدود المسموحة (${method.minAmount} - ${method.maxAmount})"
                            return@Button
                        }
                        errorMessage = null
                        showPinDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("المتابعة والتأكيد الآمن (PIN)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                    Text("رمز الحماية المالي (PIN)", color = TextPrimary)
                }
            },
            text = {
                Column {
                    Text("أدخل رمز المعاملات المكون من 4 أرقام لتأكيد الإيداع (الافتراضي: 1234):", color = TextSecondary)
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
                            errorMessage = "رمز الـ PIN غير صحيح."
                            showPinDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black)
                ) {
                    Text("تأكيد العملية", fontWeight = FontWeight.Bold)
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
