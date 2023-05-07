package com.swipecare.payments.userview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swipecare.payments.model.Userlistres
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Userviewmodel : ViewModel() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _userlistview = MutableLiveData<Userlistres>()
    val userlistview: LiveData<Userlistres>
        get() = _userlistview

    fun getAccountStatementTransactions(userId: String, appToken: String) {
        coroutineScope.launch {
            val userlistTransactionResponse =
                SwipeApi.retrofitService.getUserStatementReport(userId, appToken)
            withContext(Dispatchers.Main) {
                if (userlistTransactionResponse.status == "TXN" && userlistTransactionResponse.userslist != null) {
                    _userlistview.value =
                        userlistTransactionResponse
                } else {
                    // todo: when statusCode = "ERR" do something here
                }
            }
        }
    }
}