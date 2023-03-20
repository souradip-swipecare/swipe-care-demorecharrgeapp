package com.swipecare.payments.model.menuReports.payoutReport

import com.squareup.moshi.Json

data class AccountDetails(
    val id: Int,
    @Json(name = "cont_id")
    val contId: String,
    @Json(name = "account_type")
    val accountType: String,
    val name: String,
    val ifsc: String,
    @Json(name = "account_number")
    val accountNumber: String,
    val address: String? = null,
    @Json(name = "fund_account_id")
    val fundAccountId: String,
    @Json(name = "updated_at")
    val updatedAt: String,
    @Json(name = "created_at")
    val createdAt: String,
)
