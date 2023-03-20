package com.swipecare.payments.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AEPSBalanceEnquiryResponse(
    val status: String,
    val message: String,
    val balance: String? = null,
    val rrn: String? = null,
    val title: String? = null,
    val id: Long? = null,
    val bank: String? = null,
    val aadhar: String? = null,
    val transactionType: String? = null,
    val amount: String? = null,/*
    @Json(name = "created_at")
    val createdAt: List<>? = null,*/
) : Parcelable