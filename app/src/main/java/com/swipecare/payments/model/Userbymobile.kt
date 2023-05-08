package com.swipecare.payments.model

import com.squareup.moshi.Json

data class Userbymobile(
    @Json(name = "status")
    val status: String,
    @Json(name = "message")
    val message: String? = null,
    @Json(name = "name")
    val name: String? = null,
    @Json(name = "wallet")
    val wallet: String? = null,
    @Json(name = "userid")
    val userid: String? = null

)
