package com.swipecare.payments.utils

import android.content.Intent
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.swipecare.payments.MainActivity
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.model.UserOnboardingDetailsResponse
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun checkOnboardingStatus(activity: MainActivity) {


}

fun isUserOnboarded(onboardingStatusResponse: UserOnboardingDetailsResponse): Boolean =
    onboardingStatusResponse.message == "User Onboarded" && onboardingStatusResponse.status == "TXN"
