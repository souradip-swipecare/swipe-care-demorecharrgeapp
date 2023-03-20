package com.swipecare.payments.profit

import com.squareup.moshi.Json
import com.swipecare.payments.model.menuReports.aepsReport.AepsReportItem

data class Profitresponse(
    @Json(name = "status")
    val status: String,
    @Json(name = "message")
    val message: String? = null,
    @Json(name = "qrImage")
    val qrImage: String? = null,
    @Json(name = "signature")
    val signature: String? = null
    )

