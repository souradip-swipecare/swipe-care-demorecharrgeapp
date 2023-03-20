package com.swipecare.payments.menuReports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swipecare.payments.model.menuReports.aepsReport.AepsReportResponse
import com.swipecare.payments.model.menuReports.payoutReport.PayoutReportResponse
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AepsReportViewModel : ViewModel() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _aepsReportResponse = MutableLiveData<AepsReportResponse>()
    val aepsReportResponse: LiveData<AepsReportResponse>
        get() = _aepsReportResponse

    fun getAepsPayoutReport(userId: String, appToken: String) {
        coroutineScope.launch {
            val payoutReportResponse = SwipeApi.retrofitService.getAepsReport(
                userId, appToken
            )
            if (isReportFetchSuccessful(payoutReportResponse)) {
                withContext(Dispatchers.Main) {
                    _aepsReportResponse.value = payoutReportResponse
                }
            }
        }
    }

    private fun isReportFetchSuccessful(payoutReportResponse: AepsReportResponse) =
        payoutReportResponse.statusCode == "TXN" && payoutReportResponse.data != null
}