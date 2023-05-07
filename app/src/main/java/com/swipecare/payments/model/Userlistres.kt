package com.swipecare.payments.model

import com.squareup.moshi.Json


data class Userlistres(
    @Json(name = "status")
    val status: String,
    @Json(name = "message")
    val message: String? = null,
    @Json(name = "userslist")
    val userslist: List<Userlistitem>? = emptyList(),
)
