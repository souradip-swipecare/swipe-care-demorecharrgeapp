package com.swipecare.payments.model.menuReports.payoutReport

import com.squareup.moshi.Json

data class BankTransaction(
    val status: String,
    @Json(name = "fund_account_id")
    val fundAccountId: String,
    val mode: String,
    val amount: Float,
    val charge: Float? = null,
    val utr: String,
    @Json(name = "payout_id")
    val payoutId: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "username")
    val userName: String,
    @Json(name = "accountdetails")
    val accountDetails: AccountDetails,
)

