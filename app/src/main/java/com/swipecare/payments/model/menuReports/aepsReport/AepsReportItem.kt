package com.swipecare.payments.model.menuReports.aepsReport

import com.squareup.moshi.Json

data class AepsReportItem(
    val mobile: String,
    val aadhar: String,
    @Json(name = "txnid")
    val txnId: String,
    val bank: String,
    @Json(name = "payid")
    val payId: String,
    val status: String,
    @Json(name = "aepstype")
    val aepsType: String,
    val type: String,
    @Json(name = "created_at")
    val createdAt: String,
    val charge: Float,
    val balance: Float,
    @Json(name = "refno")
    val refNo: String? = null,
    val amount: Float
)
