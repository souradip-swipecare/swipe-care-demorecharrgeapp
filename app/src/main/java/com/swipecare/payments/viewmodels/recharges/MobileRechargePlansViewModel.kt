package com.swipecare.payments.viewmodels.recharges

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.swipecare.payments.model.OperatorAndCircle
import com.swipecare.payments.model.OperatorPlan
import com.swipecare.payments.recharges.MobileRechargeRepository
import com.swipecare.payments.repository.recharges.MobileRechargePlansRepository
import kotlinx.coroutines.*
import androidx.lifecycle.viewModelScope

class MobileRechargePlansViewModel(private val mobileRechargeRepository: MobileRechargePlansRepository) :
    ViewModel() {

    private val _operatorAndCircle = mobileRechargeRepository.operatorAndCircle
    val operatorAndCircle: LiveData<OperatorAndCircle>
        get() = _operatorAndCircle

    private val _operatorPlan = mobileRechargeRepository.operatorPlan
    val operatorPlan: LiveData<OperatorPlan>
        get() = _operatorPlan

    private val _showLoadingDialog = MutableLiveData<Boolean>()
    val showLoadingDialog: LiveData<Boolean>
        get() = _showLoadingDialog

    suspend fun getOperatorAndCircle(
        apiUserId: String,
        apiPassword: String,
        mobileNumber: String,
    ) {
        viewModelScope.launch {
            mobileRechargeRepository.getOperatorAndCircle(
                apiUserId,
                apiPassword,
                mobileNumber,
            )
        }
    }

    suspend fun getOperatorPlan(
        apiUserId: String,
        apiPassword: String,
        operatorAndCircle: OperatorAndCircle,
    ) {
        viewModelScope.launch {
            mobileRechargeRepository.getOperatorPlan(
                apiUserId,
                apiPassword,
                operatorAndCircle,
            )
        }
    }

    fun showLoadingDialog(shouldShow: Boolean) {
        _showLoadingDialog.value = shouldShow
    }
}