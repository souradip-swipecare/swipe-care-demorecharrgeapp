package com.swipecare.payments.recharges

import com.swipecare.payments.model.OperatorAndCircle

interface MobileRechargeRepository {
    suspend fun getOperatorAndCircle(mobileNumber: String): OperatorAndCircle
}