package com.swipecare.payments.fund

import com.squareup.moshi.Json

data class Upiverifyres(
    @Json(name = "status")
    val status: String,
    @Json(name = "message")
    val message: String,
)
