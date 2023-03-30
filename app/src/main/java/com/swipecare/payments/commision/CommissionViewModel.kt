package com.swipecare.payments.commision

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swipecare.payments.model.commision.CommissionDataJson
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommissionViewModel : ViewModel() {
    private val _showLoadingDialog = MutableLiveData<Boolean>()
    val showLoadingDialog: LiveData<Boolean>
        get() = _showLoadingDialog

    private val _commissionDataJson = MutableLiveData<CommissionDataJson>()
    val commissionDataJson: LiveData<CommissionDataJson>
        get() = _commissionDataJson

    fun getUserCommission(appToken: String, userId: String) {
        lateinit var commissionData: CommissionDataJson
        viewModelScope.launch {
            commissionData = SwipeApi.retrofitService.getCommissionData(appToken, userId)
            withContext(Dispatchers.Main) {
                if (commissionData.status == "TXN") {
                    _commissionDataJson.value = commissionData
                }
            }
        }
    }

    fun showLoadingDialog(shouldShow: Boolean) {
        _showLoadingDialog.value = shouldShow
    }
}
