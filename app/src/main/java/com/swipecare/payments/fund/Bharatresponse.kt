package com.swipecare.payments.fund

import com.squareup.moshi.Json

data class Bharatresponse(
    @Json(name = "status")
    val status: String,
    @Json(name = "ref_no")
    val ref_no: String,
    @Json(name = "amount")
    val amount: String,
    @Json(name = "paydate")
    val paydate: String,
    @Json(name = "message")
    val message: String
)
