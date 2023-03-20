package com.swipecare.payments.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PayoutResponse(
    val status: String,
    val message: String? = " ",
    val balance: String? = " ",
    val rrn: String? = " ",
    val transactionType: String? = " ",
    val title: String? = " ",
    val aadhar: String? = " ",
    val id: String? = " ",
    @Json(name = "amount")
    val transactionAmount: String? = " ",
    @Json(name = "created_at")
    val createdAt: String? = " ",
    val bank: String? = " ",
) : Parcelable