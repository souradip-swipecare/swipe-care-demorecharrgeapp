package com.swipecare.payments.model.menuReports.payoutReport

import com.squareup.moshi.Json

data class AccountStatementResponse(
    @Json(name = "statuscode")
    val statusCode:String,
    @Json(name = "data")
    val accountStatementTransactions: List<AccountStatementTransaction>
)