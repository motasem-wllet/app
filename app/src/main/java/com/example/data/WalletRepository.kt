package com.example.data

import androidx.lifecycle.ViewModel
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WalletViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "usr_1",
            name = "أحمد العلي (User 1)",
            email = "ahmad@nexus.com",
            phone = "+963988123456",
            walletNumber = "W-94827104",
            role = "user",
            kycStatus = "verified",
            availableBalance = 3450.00,
            heldBalance = 150.00,
            pinCode = "1234",
            avatarUrl = ""
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _allUsers = MutableStateFlow(
        listOf(
            _currentUser.value,
            UserProfile(
                id = "usr_2",
                name = "سارة محمد (User 2)",
                email = "sara@nexus.com",
                phone = "+963933654321",
                walletNumber = "W-88371209",
                role = "user",
                kycStatus = "unverified",
                availableBalance = 1200.50,
                heldBalance = 0.0,
                pinCode = "1234",
                avatarUrl = ""
            ),
            UserProfile(
                id = "adm_1",
                name = "المدير العام (Admin)",
                email = "admin@nexus.com",
                phone = "+963944000000",
                walletNumber = "W-ADMIN-01",
                role = "admin",
                kycStatus = "verified",
                availableBalance = 50000.0,
                heldBalance = 0.0,
                pinCode = "0000",
                avatarUrl = ""
            )
        )
    )
    val allUsers: StateFlow<List<UserProfile>> = _allUsers.asStateFlow()

    private val _paymentMethods = MutableStateFlow(
        listOf(
            PaymentMethod(
                id = "pm_payeer",
                name = "Payeer USD / EUR",
                category = "e-wallet",
                currency = "USD",
                iconName = "CreditCard",
                allowDeposit = true,
                allowWithdraw = true,
                depositFeePercent = 1.5,
                depositFixedFee = 0.50,
                withdrawFeePercent = 2.0,
                withdrawFixedFee = 1.0,
                minAmount = 10.0,
                maxAmount = 5000.0,
                instructions = "تحويل المبلغ إلى حساب Payeer الخاص بالمنصة: P109823411 مع ذكر رقم العملية.",
                merchantAccountDetails = "P109823411 (Nexus Financial Ltd)",
                requiredFields = listOf("Payeer Account ID", "Transfer Batch / Operation ID")
            ),
            PaymentMethod(
                id = "pm_usdt",
                name = "USDT (TRC20 - Tron)",
                category = "crypto",
                currency = "USDT",
                iconName = "CurrencyBitcoin",
                allowDeposit = true,
                allowWithdraw = true,
                depositFeePercent = 0.5,
                depositFixedFee = 1.0,
                withdrawFeePercent = 1.0,
                withdrawFixedFee = 2.0,
                minAmount = 20.0,
                maxAmount = 20000.0,
                instructions = "أرسل عملات USDT حصرياً عبر شبكة TRC20 إلى العنوان أدناه.",
                merchantAccountDetails = "TYu8v9sB72nQwRxK3mPzL8v4xYz1qWs2A9",
                requiredFields = listOf("Sender Wallet Address", "TxID Hash")
            ),
            PaymentMethod(
                id = "pm_wise",
                name = "Wise / Bank Wire",
                category = "bank",
                currency = "USD",
                iconName = "AccountBalance",
                allowDeposit = true,
                allowWithdraw = true,
                depositFeePercent = 1.0,
                depositFixedFee = 5.0,
                withdrawFeePercent = 1.5,
                withdrawFixedFee = 10.0,
                minAmount = 50.0,
                maxAmount = 10000.0,
                instructions = "قم بتحويل السويفت البنكي إلى حسابنا المصرفي الدولي الموضح أدناه.",
                merchantAccountDetails = "IBAN: GB83WISE40321098321456 / BIC: WISEGB2L",
                requiredFields = listOf("Bank Name", "Sender IBAN", "Transfer Reference Number")
            ),
            PaymentMethod(
                id = "pm_local",
                name = "محفظة محلية (Syriatel Cash / MTN)",
                category = "local",
                currency = "SYP",
                iconName = "PhoneAndroid",
                allowDeposit = true,
                allowWithdraw = true,
                depositFeePercent = 2.0,
                depositFixedFee = 0.0,
                withdrawFeePercent = 2.5,
                withdrawFixedFee = 0.0,
                minAmount = 10000.0,
                maxAmount = 2000000.0,
                instructions = "حول المبلغ عبر سيريتل كاش إلى الرقم المعتمد أدناه.",
                merchantAccountDetails = "0933123456 (Nexus Local Cashier)",
                requiredFields = listOf("Mobile Number", "SMS Receipt Code")
            )
        )
    )
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods.asStateFlow()

    private val _transactions = MutableStateFlow(
        listOf(
            Transaction(
                id = "txn_1",
                userId = "usr_1",
                type = "deposit",
                paymentMethodId = "pm_usdt",
                grossAmount = 500.0,
                feeAmount = 3.5,
                netAmount = 496.5,
                currency = "USDT",
                status = "approved",
                referenceNumber = "TXN-849201",
                receiptImageUrl = "",
                adminNote = "تم التحقق من شبكة الترون وإضافة الرصيد بنجاح.",
                createdAt = System.currentTimeMillis() - 86400000 * 2
            ),
            Transaction(
                id = "txn_2",
                userId = "usr_1",
                type = "p2p_send",
                paymentMethodId = "",
                grossAmount = 100.0,
                feeAmount = 0.5,
                netAmount = 99.5,
                currency = "USD",
                status = "approved",
                referenceNumber = "TXN-849202",
                targetUserWallet = "W-88371209",
                adminNote = "تحويل داخلي فوري",
                createdAt = System.currentTimeMillis() - 86400000
            )
        )
    )
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _p2pTransfers = MutableStateFlow<List<P2PTransfer>>(emptyList())
    val p2pTransfers: StateFlow<List<P2PTransfer>> = _p2pTransfers.asStateFlow()

    private val _paymentRequests = MutableStateFlow<List<PaymentRequest>>(
        listOf(
            PaymentRequest(
                id = "req_1",
                creatorWallet = "W-94827104",
                amount = 75.0,
                currency = "USD",
                note = "استحقاق تصميم واجهة مستخدم",
                status = "pending",
                createdAt = System.currentTimeMillis() - 3600000
            )
        )
    )
    val paymentRequests: StateFlow<List<PaymentRequest>> = _paymentRequests.asStateFlow()

    private val _kycRequests = MutableStateFlow<List<KycRequest>>(emptyList())
    val kycRequests: StateFlow<List<KycRequest>> = _kycRequests.asStateFlow()

    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(
        listOf(
            SupportTicket(
                id = "tkt_1",
                userId = "usr_1",
                transactionId = "TXN-849201",
                subject = "استفسار عن تأكيد إيداع USDT",
                message = "هل تم اعتماد تحويل الـ 500 USDT الخاص بي؟",
                status = "open",
                replies = listOf(
                    TicketReply("المدير العام", "أهلاً بك، تم الاعتماد وسيظهر رصيدك فوراً.", true)
                ),
                createdAt = System.currentTimeMillis() - 5000000
            )
        )
    )
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    private val _platformSettings = MutableStateFlow(PlatformSettings())
    val platformSettings: StateFlow<PlatformSettings> = _platformSettings.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(
        listOf(
            AuditLog("log_1", "adm_1", "SYSTEM_INIT", "تم تهيئة النظام المالي الذكي بنجاح", System.currentTimeMillis() - 100000000)
        )
    )
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    // Switch active user persona for testing
    fun switchUserPersona(userId: String) {
        val user = _allUsers.value.find { it.id == userId }
        if (user != null) {
            _currentUser.value = user
        }
    }

    // Submit Deposit
    fun submitDeposit(paymentMethodId: String, gross: Double, customFields: Map<String, String>, receiptUrl: String): Boolean {
        val pm = _paymentMethods.value.find { it.id == paymentMethodId } ?: return false
        val fee = (gross * (pm.depositFeePercent / 100.0)) + pm.depositFixedFee
        val net = gross - fee
        val txnRef = "TXN-${(100000..999999).random()}"

        val newTxn = Transaction(
            id = "txn_${System.currentTimeMillis()}",
            userId = _currentUser.value.id,
            type = "deposit",
            paymentMethodId = paymentMethodId,
            grossAmount = gross,
            feeAmount = fee,
            netAmount = net,
            currency = pm.currency,
            status = "pending",
            referenceNumber = txnRef,
            customFieldValues = customFields,
            receiptImageUrl = receiptUrl,
            createdAt = System.currentTimeMillis()
        )

        _transactions.update { listOf(newTxn) + it }
        return true
    }

    // Submit Withdraw (External)
    fun submitWithdraw(paymentMethodId: String, gross: Double, feeBearerDeduct: Boolean, customFields: Map<String, String>): Pair<Boolean, String> {
        val user = _currentUser.value
        val pm = _paymentMethods.value.find { it.id == paymentMethodId } ?: return Pair(false, "طريقة الدفع غير موجودة")
        val fee = (gross * (pm.withdrawFeePercent / 100.0)) + pm.withdrawFixedFee
        val totalDeduction = if (feeBearerDeduct) gross else (gross + fee)

        if (user.availableBalance < totalDeduction) {
            return Pair(false, "الرصيد المتاح غير كافٍ لإتمام عملية السحب مع الرسوم.")
        }

        // Lock funds into heldBalance
        val updatedUser = user.copy(
            availableBalance = user.availableBalance - totalDeduction,
            heldBalance = user.heldBalance + totalDeduction
        )
        updateUserProfile(updatedUser)

        val txnRef = "TXN-${(100000..999999).random()}"
        val newTxn = Transaction(
            id = "txn_${System.currentTimeMillis()}",
            userId = user.id,
            type = "withdraw",
            paymentMethodId = paymentMethodId,
            grossAmount = gross,
            feeAmount = fee,
            netAmount = gross - (if (feeBearerDeduct) fee else 0.0),
            currency = pm.currency,
            status = "pending",
            referenceNumber = txnRef,
            customFieldValues = customFields,
            createdAt = System.currentTimeMillis()
        )

        _transactions.update { listOf(newTxn) + it }
        return Pair(true, "تم تقديم طلب السحب بنجاح وتم تعليق المبلغ ريثما يتم تنفيذه من الإدارة.")
    }

    // P2P Instant Transfer
    fun executeP2PTransfer(recipientWallet: String, amount: Double, note: String): Pair<Boolean, String> {
        val sender = _currentUser.value
        if (sender.walletNumber == recipientWallet) {
            return Pair(false, "لا يمكنك التحويل إلى نفس محفظتك.")
        }
        val recipient = _allUsers.value.find { it.walletNumber == recipientWallet }
            ?: return Pair(false, "رقم المحفظة المستلمة غير موجود في النظام.")

        val fee = amount * (_platformSettings.value.p2pFeePercent / 100.0)
        val totalDeduction = amount + fee

        if (sender.availableBalance < totalDeduction) {
            return Pair(false, "الرصيد غير كافٍ (المبلغ + عمولة التحويل الداخلي).")
        }

        // Deduct from sender
        val updatedSender = sender.copy(availableBalance = sender.availableBalance - totalDeduction)
        // Add to recipient
        val updatedRecipient = recipient.copy(availableBalance = recipient.availableBalance + amount)

        updateUserProfile(updatedSender)
        updateOtherUser(updatedRecipient)

        val txnRef = "TXN-${(100000..999999).random()}"
        val senderTxn = Transaction(
            id = "txn_${System.currentTimeMillis()}",
            userId = sender.id,
            type = "p2p_send",
            paymentMethodId = "",
            grossAmount = amount,
            feeAmount = fee,
            netAmount = amount,
            currency = "USD",
            status = "approved",
            referenceNumber = txnRef,
            targetUserWallet = recipientWallet,
            adminNote = note,
            createdAt = System.currentTimeMillis()
        )
        val receiverTxn = Transaction(
            id = "txn_${System.currentTimeMillis() + 1}",
            userId = recipient.id,
            type = "p2p_receive",
            paymentMethodId = "",
            grossAmount = amount,
            feeAmount = 0.0,
            netAmount = amount,
            currency = "USD",
            status = "approved",
            referenceNumber = txnRef,
            targetUserWallet = sender.walletNumber,
            adminNote = note,
            createdAt = System.currentTimeMillis()
        )

        _transactions.update { listOf(senderTxn, receiverTxn) + it }
        return Pair(true, "تم التحويل بنجاح إلى المستلم ${recipient.name} بقيمة $amount USD.")
    }

    // Create Payment Request / Invoice
    fun createPaymentRequest(amount: Double, note: String) {
        val req = PaymentRequest(
            id = "req_${System.currentTimeMillis()}",
            creatorWallet = _currentUser.value.walletNumber,
            amount = amount,
            currency = "USD",
            note = note,
            status = "pending",
            createdAt = System.currentTimeMillis()
        )
        _paymentRequests.update { listOf(req) + it }
    }

    // Submit KYC
    fun submitKyc(fullName: String, docType: String, docNum: String, imgUrl: String) {
        val kyc = KycRequest(
            id = "kyc_${System.currentTimeMillis()}",
            userId = _currentUser.value.id,
            fullName = fullName,
            documentType = docType,
            documentNumber = docNum,
            documentImageUrl = imgUrl,
            status = "pending",
            submittedAt = System.currentTimeMillis()
        )
        _kycRequests.update { listOf(kyc) + it }
    }

    // Admin: Approve Transaction
    fun adminApproveTransaction(txnId: String, adminNote: String) {
        val txn = _transactions.value.find { it.id == txnId } ?: return
        if (txn.status != "pending") return

        val user = _allUsers.value.find { it.id == txn.userId } ?: return

        if (txn.type == "deposit") {
            // Add netAmount to available balance
            val updatedUser = user.copy(availableBalance = user.availableBalance + txn.netAmount)
            updateOtherUser(updatedUser)
        } else if (txn.type == "withdraw") {
            // Deduct total held amount (gross + fee)
            val totalHeld = txn.grossAmount + txn.feeAmount
            val updatedUser = user.copy(heldBalance = maxOf(0.0, user.heldBalance - totalHeld))
            updateOtherUser(updatedUser)
        }

        _transactions.update { list ->
            list.map { if (it.id == txnId) it.copy(status = "approved", adminNote = adminNote) else it }
        }

        addAuditLog("APPROVE_TRANSACTION", "تم اعتماد المعاملة ${txn.referenceNumber} بمبلغ ${txn.netAmount} ${txn.currency}")
    }

    // Admin: Reject Transaction
    fun adminRejectTransaction(txnId: String, reason: String) {
        val txn = _transactions.value.find { it.id == txnId } ?: return
        if (txn.status != "pending") return

        val user = _allUsers.value.find { it.id == txn.userId } ?: return

        if (txn.type == "withdraw") {
            // Return held funds back to available balance
            val totalHeld = txn.grossAmount + txn.feeAmount
            val updatedUser = user.copy(
                availableBalance = user.availableBalance + totalHeld,
                heldBalance = maxOf(0.0, user.heldBalance - totalHeld)
            )
            updateOtherUser(updatedUser)
        }

        _transactions.update { list ->
            list.map { if (it.id == txnId) it.copy(status = "rejected", adminNote = reason) else it }
        }

        addAuditLog("REJECT_TRANSACTION", "تم رفض المعاملة ${txn.referenceNumber}. السبب: $reason")
    }

    // Admin: Manual Balance Adjustment
    fun adminAdjustBalance(userId: String, amountDelta: Double, reason: String): Boolean {
        val user = _allUsers.value.find { it.id == userId } ?: return false
        val newBalance = user.availableBalance + amountDelta
        if (newBalance < 0) return false

        val updatedUser = user.copy(availableBalance = newBalance)
        updateOtherUser(updatedUser)
        addAuditLog("MANUAL_BALANCE_ADJUST", "تم تعديل رصيد المستخدم ${user.name} بقيمة $amountDelta. السبب: $reason")
        return true
    }

    // Admin: Add or Edit Payment Method
    fun savePaymentMethod(method: PaymentMethod) {
        _paymentMethods.update { list ->
            val index = list.indexOfFirst { it.id == method.id }
            if (index >= 0) {
                list.toMutableList().apply { set(index, method) }
            } else {
                list + method
            }
        }
        addAuditLog("SAVE_PAYMENT_METHOD", "تم حفظ طريقة الدفع: ${method.name}")
    }

    fun deletePaymentMethod(methodId: String) {
        _paymentMethods.update { list -> list.filter { it.id != methodId } }
        addAuditLog("DELETE_PAYMENT_METHOD", "تم حذف طريقة الدفع ID: $methodId")
    }

    fun updatePlatformSettings(settings: PlatformSettings) {
        _platformSettings.value = settings
        addAuditLog("UPDATE_SETTINGS", "تم تحديث إعدادات المنصة العامة")
    }

    private fun updateUserProfile(updated: UserProfile) {
        _currentUser.value = updated
        updateOtherUser(updated)
    }

    private fun updateOtherUser(updated: UserProfile) {
        _allUsers.update { list ->
            list.map { if (it.id == updated.id) updated else it }
        }
    }

    private fun addAuditLog(action: String, details: String) {
        val log = AuditLog(
            id = "log_${System.currentTimeMillis()}",
            adminId = _currentUser.value.id,
            action = action,
            details = details,
            timestamp = System.currentTimeMillis()
        )
        _auditLogs.update { listOf(log) + it }
    }
}
