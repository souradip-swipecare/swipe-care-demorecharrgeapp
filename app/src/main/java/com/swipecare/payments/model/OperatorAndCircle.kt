package com.swipecare.payments.model

import com.squareup.moshi.Json

data class OperatorAndCircle(
    @Json(name = "ERROR")
    val error: String,
    @Json(name = "STATUS")
    val status: String,
    @Json(name = "Mobile")
    val mobile: String?,
    @Json(name = "Operator")
    val operator: String,
    @Json(name = "OpCode")
    val operatorCode: String?,
    @Json(name = "Circle")
    val circle: String?,
    @Json(name = "CircleCode")
    val circleCode:String,
    @Json(name = "Message")
    val message: String
)
