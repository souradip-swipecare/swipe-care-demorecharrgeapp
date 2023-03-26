package com.swipecare.payments.model.dth

import com.squareup.moshi.Json

data class Dthinfodata(
    @Json(name = "Name")
    val Name: String,
    @Json(name = "Balance")
    val Balance: String,
    @Json(name = "Next Recharge Date")
    val rechargeamount: String,
    @Json(name= "Plan")
    val Plan :String
)
