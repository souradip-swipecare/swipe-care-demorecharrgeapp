package com.swipecare.payments.model

import com.squareup.moshi.Json

data class OperatorPlan(
    @Json(name = "ERROR")
    val error: String,
    @Json(name = "STATUS")
    val status: String,
    @Json(name = "Operator")
    val Operator: String,
    @Json(name = "Circle")
    val circle: String,
    @Json(name = "RDATA")
    val Rdata: PlanTypes
)