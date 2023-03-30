package com.swipecare.payments.model.commision

import com.squareup.moshi.Json

data class CommissionDataJson(
    val status: String,
    @Json(name = "mycommision")
    val myCommission: MyCommission,
)

data class MyCommission(
    val commission: Commission,
)
