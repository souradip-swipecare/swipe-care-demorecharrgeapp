package com.swipecare.payments.fund

import com.squareup.moshi.Json

data class Upiresponse(
    @Json(name = "status")
    val status: String,
    @Json(name = "message")
    val message: String,
)
