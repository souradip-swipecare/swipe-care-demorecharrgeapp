package com.swipecare.payments.model.Recrecipt

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Reciptforwd(
val status: String?=null,
val number: String?=null,
val refno: String? = null,
val date: String?=null,
val amount: String?=null,
val provider: String?=null,
) : Parcelable

