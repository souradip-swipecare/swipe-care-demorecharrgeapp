package com.swipecare.payments.model.commision

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Commission(
    val mobile: List<CommissionData>? = emptyList(),
    val dth: List<CommissionData>? = emptyList(),
) : Parcelable {
    fun getCommissionDataAsDictionary(): Map<String, List<CommissionData>?> {
        return linkedMapOf(
            "mobile" to mobile,
            "dth" to dth,
        )
    }
    fun getSize(): Int = 16

}
