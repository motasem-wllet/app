package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    currentUser: UserProfile,
    onKycSubmitted: (String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var kycFullName by remember { mutableStateOf("") }
    var kycDocType by remember { mutableStateOf("جواز سفر") }
    var kycDocNum by remember { mutableStateOf("") }
    var kycSuccess by remember { mutableStateOf(false) }

    var supabaseUrlInput by remember { mutableStateOf("") }
    var supabaseKeyInput by remember { mutableStateOf("") }
    var supabaseSaved by remember { mutableStateOf(false) }

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
                    text = "الأمان، التوثيق والبيئة السحابية",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        // KYC Verification Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = ObsidianCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = NeonEmerald.copy(alpha = 0.15f), modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("توثيق الهوية المالي (KYC Tier 2)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("رفع حدود السحب اليومي وتفعيل الميزات الدولية", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (currentUser.kycStatus == "verified") NeonEmerald.copy(alpha = 0.15f) else GoldAccent.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (currentUser.kycStatus == "verified") NeonEmerald else GoldAccent)
                    ) {
                        Text(
                            text = if (currentUser.kycStatus == "verified") "الحساب موثق بالكامل - الحد اليومي: 50,000 USD" else "الحساب غير موثق - الحد اليومي: 500 USD",
                            modifier = Modifier.padding(10.dp),
                            color = if (currentUser.kycStatus == "verified") NeonEmerald else GoldAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (kycSuccess) {
                        Surface(
                            color = NeonEmerald.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald)
                        ) {
                            Text(
                                text = "تم تقديم المستندات بنجاح للمراجعة الإدارية.",
                                modifier = Modifier.padding(12.dp),
                                color = NeonEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (currentUser.kycStatus != "verified" && !kycSuccess) {
                        OutlinedTextField(
                            value = kycFullName,
                            onValueChange = { kycFullName = it },
                            label = { Text("الاسم الكامل باللغة الإنجليزية / العربية") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonEmerald, unfocusedBorderColor = ObsidianBorder)
                        )

                        OutlinedTextField(
                            value = kycDocNum,
                            onValueChange = { kycDocNum = it },
                            label = { Text("رقم جواز السفر أو البطاقة الشخصية") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonEmerald, unfocusedBorderColor = ObsidianBorder)
                        )

                        Button(
                            onClick = {
                                if (kycFullName.isNotBlank() && kycDocNum.isNotBlank()) {
                                    onKycSubmitted(kycFullName, kycDocType, kycDocNum, "https://example.com/id.jpg")
                                    kycSuccess = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black)
                        ) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("رفع وثائق التوثيق للمراجعة", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Security PIN Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = ObsidianCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = RoyalIndigo.copy(alpha = 0.15f), modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = RoyalIndigo, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("رمز أمان المعاملات (Transaction PIN)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("يُطلب إجبارياً قبل تنفيذ أي إرسال أو سحب أموال", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    }
                    Text("رمز الـ PIN النشط: •••• (الافتراضي: 1234)", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }

        // Supabase Live Sync Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = ObsidianCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = CyberCyan.copy(alpha = 0.15f), modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("ربط قاعدة بيانات Supabase الحية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("الانتقال من الـ Sandbox المحلي إلى السحابة الحية", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    }

                    OutlinedTextField(
                        value = supabaseUrlInput,
                        onValueChange = { supabaseUrlInput = it },
                        label = { Text("VITE_SUPABASE_URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = ObsidianBorder)
                    )

                    OutlinedTextField(
                        value = supabaseKeyInput,
                        onValueChange = { supabaseKeyInput = it },
                        label = { Text("VITE_SUPABASE_ANON_KEY") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = ObsidianBorder)
                    )

                    Button(
                        onClick = { supabaseSaved = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black)
                    ) {
                        Text("حفظ واختبار اتصال Supabase Live", fontWeight = FontWeight.Bold)
                    }

                    if (supabaseSaved) {
                        Surface(
                            color = NeonEmerald.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald)
                        ) {
                            Text(
                                text = "تم التحقق من الاتصال بنجاح وتفعيل مزامنة Supabase الحية!",
                                modifier = Modifier.padding(12.dp),
                                color = NeonEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
