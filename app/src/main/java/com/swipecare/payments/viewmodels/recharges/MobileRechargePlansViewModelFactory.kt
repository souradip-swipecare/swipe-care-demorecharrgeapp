package com.swipecare.payments.viewmodels.recharges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swipecare.payments.repository.recharges.MobileRechargePlansRepository
import java.lang.IllegalArgumentException

class MobileRechargePlansViewModelFactory(private val mobileRechargePlansRepository: MobileRechargePlansRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MobileRechargePlansViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MobileRechargePlansViewModel(mobileRechargePlansRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}