package com.swipecare.payments.model.bodypara

 data class CahwithdrawBody(
    val user_id: String,
    val apptoken: String,
    val mobileNumber: String,
    val adhaarNumber: String,
    val nationalBankIdentificationNumber: String,
    val transactionAmount: String,
    val biodata: String
)