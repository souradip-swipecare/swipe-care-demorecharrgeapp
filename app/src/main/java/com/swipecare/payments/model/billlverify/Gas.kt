package com.swipecare.payments.model.billlverify

 data class Gas(
     val status: String,
     val statuscode: String,
     val name: String?= null,
     val amount : String? = null,

     )