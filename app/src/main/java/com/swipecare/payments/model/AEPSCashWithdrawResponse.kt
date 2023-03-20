package com.swipecare.payments.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AEPSCashWithdrawResponse(
    val status: String,
    val message: String,
    val balance: String? = null,
    val rrn: String? = null,
    val transactionType: String? = null,
    val title: String? = null,
    val aadhar: String? = null,
    val id: String? = null,
    val amount: String? = null,
    /*@Json(name = "created_at")
    val createdAt: List<String>?*/
    val bank: String? = null
): Parcelable
