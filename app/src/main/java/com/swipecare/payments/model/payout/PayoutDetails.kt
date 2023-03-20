package com.swipecare.payments.model.payout

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PayoutDetails(
    val customerName: String,
    val customerMobile: String,
    val accountNumber: String,
    val ifscCode: String,
    val amount: String,
    val paymentMode: PaymentMode? = null,
) : Parcelable