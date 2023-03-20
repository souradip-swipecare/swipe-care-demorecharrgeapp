package com.swipecare.payments.model.bodypara

data class MinistBody(
    val user_id: String,
    val apptoken: String,
    val mobileNumber: String,
    val adhaarNumber: String,
    val nationalBankIdentificationNumber: String,
    val biodata: String
)