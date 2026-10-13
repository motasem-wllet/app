package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.WalletViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NexusWalletTheme(darkTheme = true) {
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
                val transactions by viewModel.transactions.collectAsStateWithLifecycle()
                val paymentMethods by viewModel.paymentMethods.collectAsStateWithLifecycle()

                var currentScreen by remember { mutableStateOf("dashboard") }

                BackHandler(enabled = currentScreen != "dashboard") {
                    currentScreen = "dashboard"
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    containerColor = ObsidianBg,
                    topBar = {
                        Column {
                            DeveloperToolbar(
                                currentUser = currentUser,
                                allUsers = allUsers,
                                onSwitchUser = { userId ->
                                    viewModel.switchUserPersona(userId)
                                    currentScreen = "dashboard"
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (currentScreen != "deposit" && currentScreen != "withdraw") {
                            NavigationBar(
                                containerColor = ObsidianSurface,
                                contentColor = TextPrimary
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen == "dashboard",
                                    onClick = { currentScreen = "dashboard" },
                                    icon = {
                                        Icon(
                                            Icons.Default.Dashboard,
                                            contentDescription = null,
                                            tint = if (currentScreen == "dashboard") NeonEmerald else TextMuted
                                        )
                                    },
                                    label = { Text("الرئيسية", color = if (currentScreen == "dashboard") NeonEmerald else TextMuted) },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = NeonEmerald.copy(alpha = 0.15f)
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "transactions",
                                    onClick = { currentScreen = "transactions" },
                                    icon = {
                                        Icon(
                                            Icons.Default.ReceiptLong,
                                            contentDescription = null,
                                            tint = if (currentScreen == "transactions") NeonEmerald else TextMuted
                                        )
                                    },
                                    label = { Text("السجل", color = if (currentScreen == "transactions") NeonEmerald else TextMuted) },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = NeonEmerald.copy(alpha = 0.15f)
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "settings",
                                    onClick = { currentScreen = "settings" },
                                    icon = {
                                        Icon(
                                            Icons.Default.Settings,
                                            contentDescription = null,
                                            tint = if (currentScreen == "settings") NeonEmerald else TextMuted
                                        )
                                    },
                                    label = { Text("الأمان", color = if (currentScreen == "settings") NeonEmerald else TextMuted) },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = NeonEmerald.copy(alpha = 0.15f)
                                    )
                                )
                                if (currentUser.role == "admin") {
                                    NavigationBarItem(
                                        selected = currentScreen == "admin",
                                        onClick = { currentScreen = "admin" },
                                        icon = {
                                            Icon(
                                                Icons.Default.AdminPanelSettings,
                                                contentDescription = null,
                                                tint = if (currentScreen == "admin") CyberCyan else TextMuted
                                            )
                                        },
                                        label = { Text("الإدارة", color = if (currentScreen == "admin") CyberCyan else TextMuted) },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = CyberCyan.copy(alpha = 0.15f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(ObsidianBg)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                (fadeIn(tween(240)) + slideInHorizontally(tween(240)) { it / 4 }) togetherWith
                                (fadeOut(tween(180)) + slideOutHorizontally(tween(180)) { -it / 4 })
                            },
                            label = "screen_transition"
                        ) { screen ->
                            when (screen) {
                                "dashboard" -> UserDashboardScreen(
                                    currentUser = currentUser,
                                    transactions = transactions,
                                    onNavigateDeposit = { currentScreen = "deposit" },
                                    onNavigateWithdraw = { currentScreen = "withdraw" },
                                    onNavigateP2P = { currentScreen = "withdraw" },
                                    onNavigateRequest = { currentScreen = "withdraw" },
                                    onViewReceipt = { currentScreen = "transactions" }
                                )
                                "deposit" -> DepositScreen(
                                    currentUser = currentUser,
                                    paymentMethods = paymentMethods,
                                    onDepositSubmitted = { pmId, gross, fields, receiptUrl ->
                                        viewModel.submitDeposit(pmId, gross, fields, receiptUrl)
                                    },
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "withdraw" -> WithdrawSendScreen(
                                    currentUser = currentUser,
                                    allUsers = allUsers,
                                    paymentMethods = paymentMethods,
                                    onWithdrawSubmitted = { pmId, gross, feeBearer, fields ->
                                        viewModel.submitWithdraw(pmId, gross, feeBearer, fields)
                                    },
                                    onP2PTransfer = { recipientWallet, amount, note ->
                                        viewModel.executeP2PTransfer(recipientWallet, amount, note)
                                    },
                                    onCreatePaymentRequest = { amount, note ->
                                        viewModel.createPaymentRequest(amount, note)
                                    },
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "transactions" -> TransactionsScreen(
                                    currentUser = currentUser,
                                    transactions = transactions,
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "settings" -> SettingsScreen(
                                    currentUser = currentUser,
                                    onKycSubmitted = { name, docType, docNum, imgUrl ->
                                        viewModel.submitKyc(name, docType, docNum, imgUrl)
                                    },
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "admin" -> AdminDashboardScreen(
                                    allUsers = allUsers,
                                    transactions = transactions,
                                    paymentMethods = paymentMethods,
                                    onApproveTransaction = { txnId, note ->
                                        viewModel.adminApproveTransaction(txnId, note)
                                    },
                                    onRejectTransaction = { txnId, reason ->
                                        viewModel.adminRejectTransaction(txnId, reason)
                                    },
                                    onAdjustBalance = { userId, delta, reason ->
                                        viewModel.adminAdjustBalance(userId, delta, reason)
                                    },
                                    onSavePaymentMethod = { pm ->
                                        viewModel.savePaymentMethod(pm)
                                    },
                                    onBack = { currentScreen = "dashboard" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
