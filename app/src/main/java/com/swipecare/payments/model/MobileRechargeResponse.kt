package com.swipecare.payments.model

data class MobileRechargeResponse(
    val status: String,
    val payid: String?,
    val refno: String?,
    val description: String?,
    val txnid: String?,
    val rrn: String?,
)