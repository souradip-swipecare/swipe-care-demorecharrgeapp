package com.swipecare.payments.model.menuReports.aepsReport

import com.squareup.moshi.Json

data class AepsReportResponse(
    @Json(name = "statuscode")
    val statusCode: String,
    val data: List<AepsReportItem>? = emptyList(),
)