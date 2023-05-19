package com.swipecare.payments.model.menuReports.payoutReport

import com.squareup.moshi.Json

data class AccountStatementTransaction(
    val number: String? = null,
    val mobile: String? = null,
    @Json(name = "txnid")
    val txnId: String? = null,
    @Json(name = "refno")
    val refNo: String? = null,
    val profit: Float? = null,
    val status: String? = null,
    val balance: Float? = null,
    val product: String? = null,
    @Json(name = "created_at")
    val createdAt: String? = null,
    @Json(name = "providername")
    val providerName: String? = null,
    @Json(name = "trans_type")
    val transactionType: String? = null,
    val amount: Float? = null,
)
