package com.swipecare.payments.repository.recharges

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.swipecare.payments.model.OperatorAndCircle
import com.swipecare.payments.model.OperatorPlan
import com.swipecare.payments.network.retrofit.PlanApi
import kotlinx.coroutines.*

class MobileRechargePlansRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _operatorAndCircle = MutableLiveData<OperatorAndCircle>()
    val operatorAndCircle: LiveData<OperatorAndCircle>
        get() = _operatorAndCircle
    private val _operatorPlan = MutableLiveData<OperatorPlan>()
    val operatorPlan: LiveData<OperatorPlan>
        get() = _operatorPlan

    suspend fun getOperatorAndCircle(
        apiUserId: String,
        apiPassword: String,
        mobileNumber: String,
    ) {
        coroutineScope.launch {
            val operatorAndCircle = PlanApi.planRetrofitService.getOperatorAndCircle(
                apiUserId,
                apiPassword,
                mobileNumber
            )
            withContext(Dispatchers.Main) {
                _operatorAndCircle.value = operatorAndCircle
            }
        }
    }

    suspend fun getOperatorPlan(
        apiUserId: String,
        apiPassword: String,
        operatorAndCircle: OperatorAndCircle,
    ) {
        coroutineScope.launch {
            val plans = PlanApi.planRetrofitService.getOperatorPlans(
                apiUserId,
                apiPassword,
                operatorAndCircle.circleCode,
                operatorAndCircle.operatorCode ?: ""
            )
            withContext(Dispatchers.Main) {
                _operatorPlan.value = plans
            }
        }
    }
}