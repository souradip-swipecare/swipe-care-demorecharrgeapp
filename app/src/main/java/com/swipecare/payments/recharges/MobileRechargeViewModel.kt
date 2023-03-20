package com.swipecare.payments.recharges

import androidx.lifecycle.ViewModel
import com.swipecare.payments.model.OperatorAndCircle

class MobileRechargeViewModel(private val repository: MobileRechargeRepository) : ViewModel() {

    suspend fun getOperatorAndCircle(mobileNumber: String): OperatorAndCircle {
        return repository.getOperatorAndCircle(mobileNumber)
    }

    override fun onCleared() {
        super.onCleared()
    }
}