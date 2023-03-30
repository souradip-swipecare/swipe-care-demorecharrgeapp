package com.swipecare.payments.model.commision

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommissionData(
    val retailer: Float? = null,
    val md: Float? = null,
    val dristributor: Float? = null,
    val whitelabel: Float? = null,
    val type: String,
    val provider: ProviderData,
): Parcelable

@Parcelize
data class ProviderData(
    val name: String,
): Parcelable
