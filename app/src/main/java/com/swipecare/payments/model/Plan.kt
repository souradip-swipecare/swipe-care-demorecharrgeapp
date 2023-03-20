package com.swipecare.payments.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Plan(
    @Json(name = "rs")
    val price: Int,
    val validity: String,
    @Json(name = "desc")
    val planDescription: String,
    @Json(name = "Type")
    val planType: String,
) : Parcelable
