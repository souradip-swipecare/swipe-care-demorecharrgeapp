package com.swipecare.payments.model.forget

import com.squareup.moshi.Json

data class Otpfrsubmit(
    @Json(name = "status")
    val status: String,
    @Json(name = "message")
    val message: String? = null

)
