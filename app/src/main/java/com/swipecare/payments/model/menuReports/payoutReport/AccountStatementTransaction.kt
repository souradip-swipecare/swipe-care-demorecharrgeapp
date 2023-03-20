package com.swipecare.payments.model.menuReports.payoutReport

import com.squareup.moshi.Json

data class AccountStatementTransaction(
    val number: String,
    val mobile: String,
    @Json(name = "txnid")
    val txnId: String,
    @Json(name = "refno")
    val refNo: String? = null,
    val profit: Float,
    val status: String,
    val balance: Float,
    val product: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "providername")
    val providerName: String,
    @Json(name = "trans_type")
    val transactionType: String,
    val amount: Float,
)
