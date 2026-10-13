package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CurrencyExchangeWidget(
    exchangeRates: Map<String, Double> = mapOf("USD" to 1.0, "EUR" to 0.92, "USDT" to 1.0, "SYP" to 13000.0)
) {
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("USDT") }
    var inputAmount by remember { mutableStateOf("100") }
    var isRotated by remember { mutableStateOf(false) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isRotated) 180f else 0f,
        animationSpec = tween(400),
        label = "swap_rotate"
    )

    val fromRate = exchangeRates[fromCurrency] ?: 1.0
    val toRate = exchangeRates[toCurrency] ?: 1.0
    val amountNum = inputAmount.toDoubleOrNull() ?: 0.0
    // Conversion: (amount / fromRate) * toRate
    val convertedAmount = (amountNum / fromRate) * toRate
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "محول العملات اللحظي (Live FX)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "أسعار الصرف المباشرة",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan
                )
            }

            // Input Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ObsidianSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "المبلغ للتحويل", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        OutlinedTextField(
                            value = inputAmount,
                            onValueChange = { inputAmount = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
                            )
                        )
                    }

                    CurrencyBadge(currency = fromCurrency) {
                        fromCurrency = when (fromCurrency) {
                            "USD" -> "USDT"
                            "USDT" -> "EUR"
                            "EUR" -> "SYP"
                            else -> "USD"
                        }
                    }
                }
            }

            // Swap Button Center
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = NeonEmerald.copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, NeonEmerald.copy(alpha = 0.4f), CircleShape)
                        .clickable {
                            isRotated = !isRotated
                            val temp = fromCurrency
                            fromCurrency = toCurrency
                            toCurrency = temp
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "تبديل",
                            tint = NeonEmerald,
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(rotationAngle)
                        )
                    }
                }
            }

            // Output Result Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ObsidianSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "القيمة المعادلة المقدرة", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            text = "${numberFormat.format(convertedAmount)} $toCurrency",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald
                        )
                    }

                    CurrencyBadge(currency = toCurrency) {
                        toCurrency = when (toCurrency) {
                            "USDT" -> "USD"
                            "USD" -> "EUR"
                            "EUR" -> "SYP"
                            else -> "USDT"
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyBadge(currency: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = RoyalIndigo.copy(alpha = 0.2f),
        modifier = Modifier
            .border(1.dp, RoyalIndigo.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currency,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "▼", fontSize = 10.sp, color = TextMuted)
        }
    }
}
