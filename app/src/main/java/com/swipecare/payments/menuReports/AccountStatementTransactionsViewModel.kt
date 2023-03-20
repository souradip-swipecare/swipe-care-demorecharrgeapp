package com.swipecare.payments.menuReports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swipecare.payments.model.menuReports.payoutReport.AccountStatementTransaction
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountStatementTransactionsViewModel : ViewModel() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _accountStatementTransaction = MutableLiveData<List<AccountStatementTransaction>>()
    val accountStatementTransaction: LiveData<List<AccountStatementTransaction>>
        get() = _accountStatementTransaction

    fun getAccountStatementTransactions(userId: String, appToken: String) {
        coroutineScope.launch {
            val accountStatementTransactionResponse =
                SwipeApi.retrofitService.getAccountStatementReport(userId, appToken)
            withContext(Dispatchers.Main) {
                if (accountStatementTransactionResponse.statusCode == "TXN" && accountStatementTransactionResponse.accountStatementTransactions.isNotEmpty()) {
                    _accountStatementTransaction.value =
                        accountStatementTransactionResponse.accountStatementTransactions
                } else {
                    // todo: when statusCode = "ERR" do something here
                }
            }
        }
    }
}