package com.swipecare.payments.fund

import com.squareup.moshi.Json

data class Bharatresponse(
    @Json(name = "status")
    val status: String,
    @Json(name = "ref_no")
    val ref_no: String? = null,
    @Json(name = "amount")
    val amount: String? = null,
    @Json(name = "paydate")
    val paydate: String? = null,
    @Json(name = "message")
    val message: String? = null
)
