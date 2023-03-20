package com.swipecare.payments.model.menuReports.payoutReport

import com.squareup.moshi.Json

data class PayoutReportResponse(
    @Json(name = "statuscode")
    val statusCode: String,
    val pages: Int? = null,
    @Json(name = "data")
    val bankTransactions: List<BankTransaction>? = emptyList(),
)