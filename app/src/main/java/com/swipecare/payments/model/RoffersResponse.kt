package com.swipecare.payments.model

import com.squareup.moshi.Json

data class RoffersResponse(
    @Json(name = "ERROR")
    val error: String,
    @Json(name = "STATUS")
    val status: String,
    @Json(name = "RDATA")
    val offersData: List<Offer>?,
    @Json(name = "MESSAGE")
    val message: String,
)

data class Offer(
    val price: String,
    val commissionUnit: String,
    val ofrtext: String,
    val logdesc: String,
    val commissionAmount: String,
)