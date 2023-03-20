package com.swipecare.payments.recharges

import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.model.OperatorAndCircle
import com.swipecare.payments.network.retrofit.PlanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MobileRechargeRepositoryImpl(private val sharedPreferences: SharePrfeManager) :
    MobileRechargeRepository {
    override suspend fun getOperatorAndCircle(mobileNumber: String): OperatorAndCircle {
        return withContext(Dispatchers.IO) {
            PlanApi.planRetrofitService.getOperatorAndCircle(
                sharedPreferences.apiUserId,
                sharedPreferences.apiPassword,
                mobileNumber = mobileNumber
            )
        }
    }
}