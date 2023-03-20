package com.swipecare.payments.fund

data class Bharatverify(
    val user_id: String,
    val apptoken: String,
    val utr: String,
    val signature : String
)
