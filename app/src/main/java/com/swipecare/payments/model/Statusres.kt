package com.swipecare.payments.model

import com.squareup.moshi.Json

data class Statusres(
    @Json(name = "status")
    val status: String,
    @Json(name = "refno")
    val refno: String? = null,
    @Json(name = "message")
    val message: String? = null
)
