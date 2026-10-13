package com.example.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val walletNumber: String, // e.g. "W-94827104"
    val role: String, // "user" or "admin"
    val kycStatus: String, // "unverified", "pending", "verified"
    val availableBalance: Double,
    val heldBalance: Double,
    val pinCode: String = "1234",
    val avatarUrl: String = ""
)

data class PaymentMethod(
    val id: String,
    val name: String,
    val category: String, // "e-wallet", "crypto", "bank", "local"
    val currency: String, // "USD", "EUR", "SYP", "USDT"
    val iconName: String,
    val allowDeposit: Boolean,
    val allowWithdraw: Boolean,
    val depositFeePercent: Double,
    val depositFixedFee: Double,
    val withdrawFeePercent: Double,
    val withdrawFixedFee: Double,
    val minAmount: Double,
    val maxAmount: Double,
    val instructions: String,
    val merchantAccountDetails: String,
    val requiredFields: List<String> // e.g. ["sender_name", "transfer_reference", "receipt_image"]
)

data class Transaction(
    val id: String,
    val userId: String,
    val type: String, // "deposit", "withdraw", "p2p_send", "p2p_receive", "invoice_pay"
    val paymentMethodId: String,
    val grossAmount: Double,
    val feeAmount: Double,
    val netAmount: Double,
    val currency: String,
    val status: String, // "pending", "approved", "rejected"
    val referenceNumber: String, // "TXN-94812"
    val targetUserWallet: String = "",
    val customFieldValues: Map<String, String> = emptyMap(),
    val receiptImageUrl: String = "",
    val adminNote: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class P2PTransfer(
    val id: String,
    val senderWallet: String,
    val recipientWallet: String,
    val amount: Double,
    val currency: String,
    val fee: Double,
    val note: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class PaymentRequest(
    val id: String,
    val creatorWallet: String,
    val amount: Double,
    val currency: String,
    val note: String,
    val status: String, // "pending", "paid"
    val createdAt: Long = System.currentTimeMillis()
)

data class KycRequest(
    val id: String,
    val userId: String,
    val fullName: String,
    val documentType: String, // "Passport", "National ID", "Driver License"
    val documentNumber: String,
    val documentImageUrl: String,
    val status: String, // "pending", "approved", "rejected"
    val submittedAt: Long = System.currentTimeMillis()
)

data class SupportTicket(
    val id: String,
    val userId: String,
    val transactionId: String,
    val subject: String,
    val message: String,
    val status: String, // "open", "resolved"
    val replies: List<TicketReply> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class TicketReply(
    val senderName: String,
    val message: String,
    val isAdmin: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class PlatformSettings(
    val p2pFeePercent: Double = 0.5,
    val dailyWithdrawLimit: Double = 5000.0,
    val generalAnnouncement: String = "Welcome to Nexus Enterprise Global FinTech Platform. All systems operational.",
    val exchangeRates: Map<String, Double> = mapOf("USD" to 1.0, "EUR" to 0.92, "SYP" to 13000.0, "USDT" to 1.0),
    val supabaseUrl: String = "",
    val supabaseAnonKey: String = ""
)

data class AuditLog(
    val id: String,
    val adminId: String,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
