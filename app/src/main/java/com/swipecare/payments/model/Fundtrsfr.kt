package com.swipecare.payments.model

import com.squareup.moshi.Json

data class Fundtrsfr(
    @Json(name = "status")
    val status: String,
    @Json(name = "message")
    val message: String? = null

)
