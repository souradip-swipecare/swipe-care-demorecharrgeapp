package com.swipecare.payments.model

import com.squareup.moshi.Json

data class Userlistitem(
    val mobile: String,
    val id: String,
    @Json(name = "name")
    val name: String?= null,
    @Json(name = "email")
    val email: String?= null,
    val mainwallet: String,
    @Json(name = "role_id")
    val role_id: String,
    val status: String,
    @Json(name = "dstock")
    val dstock: String,
    val rstock: String,
    @Json(name = "mstock")
    val mstock: String,
    val kyc: String

)
