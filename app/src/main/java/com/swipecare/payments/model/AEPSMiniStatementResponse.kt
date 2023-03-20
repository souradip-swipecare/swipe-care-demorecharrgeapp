package com.swipecare.payments.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.math.BigInteger

@Parcelize
data class AEPSMiniStatementResponse(
    val status: String,
    val message: String,
    val balance: String? = null,
    val rrn: String? = null,
    val transactionType:  String? = null,
    val title: String? = null,
    val aadhar: String? = null,
    val id: Long? = null,
    @Json(name = "created_at")
    val createdAt: String? = null,
    val bank: String? = null,
    val data: List<Transaction>? = null
): Parcelable