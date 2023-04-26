package com.swipecare.payments.model

import com.squareup.moshi.Json

data class Useradd(
    @Json(name = "status")
    val status: String,

    @Json(name = "message")
    val message: String? = null
)
