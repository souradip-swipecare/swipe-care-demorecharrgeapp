package com.swipecare.payments.model

import com.squareup.moshi.Json

data class UserOnboardingDetailsResponse(
    val status: String,
    val message: String,
    val data: OnBoardingData?,
)

data class OnBoardingData(
    @Json(name = "bc_id")
    val merchantId: String,
    val user: UserData,
    @Json(name = "shopname")
    val shopName: String
)

data class UserData(
    val company: Company,
)

data class Company(
    @Json(name = "shortname")
    val shortName: String?,
)

