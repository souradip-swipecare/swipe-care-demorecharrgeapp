package com.swipecare.payments.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaction(
    val date: String,
    val amount: Float,
    val txnType: String,
    @Json(name = "narration")
    val description: String
): Parcelable