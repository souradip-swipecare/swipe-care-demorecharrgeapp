package com.swipecare.payments.model.payout

import com.squareup.moshi.Json

data class OtpSentStatus(
    @Json(name = "statuscode") val statusCode: String? = null,
    val status: String? = null,
    val message: String? = null,
)