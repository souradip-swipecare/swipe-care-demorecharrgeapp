package com.swipecare.payments.model.dth

import com.squareup.moshi.Json

data class Dthinfoores(
    @Json(name = "statuscode")
    val statuscode: String,
    @Json(name = "message")
    val message: String? = null,
    @Json(name = "data")
    val dthinfodata: List<Dthinfodata>
)
