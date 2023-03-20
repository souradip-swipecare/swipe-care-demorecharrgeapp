package com.swipecare.payments.menuReports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swipecare.payments.model.menuReports.payoutReport.PayoutReportResponse
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PayoutReportViewModel : ViewModel() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _payoutReportResponse = MutableLiveData<PayoutReportResponse>()
    val payoutReportResponse: LiveData<PayoutReportResponse>
        get() = _payoutReportResponse

    var nextPage: Int = 0
        private set
    var prevPage: Int = nextPage
        private set
    val start = 0

    fun getPayoutReports(userId: String, appToken: String, page: Int) {
        coroutineScope.launch {
            val payoutReportResponse = SwipeApi.retrofitService.getPayoutReport(
                userId, appToken, page
            )
            if (isReportFetchSuccessful(payoutReportResponse)) {
                withContext(Dispatchers.Main) {
                    _payoutReportResponse.value = payoutReportResponse
                }
            }
        }
    }

    fun onNextClick() {
        nextPage++
        prevPage++
    }

    fun onPreviousClick() {
        nextPage--
        prevPage--
    }

    private fun isReportFetchSuccessful(payoutReportResponse: PayoutReportResponse) =
        payoutReportResponse.statusCode == "TXN" && payoutReportResponse.bankTransactions != null
}