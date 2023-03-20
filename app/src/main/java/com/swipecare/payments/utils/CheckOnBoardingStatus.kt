package com.swipecare.payments.utils

import android.content.Intent
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.paysprint.onboardinglib.activities.HostActivity
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

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    val sharePrfeManager = SharePrfeManager.getInstance(activity)
    val userId = sharePrfeManager.mGetUserId()
    val appToken = sharePrfeManager.mGetappToken()

    coroutineScope.launch {
        val onboardingStatusDetails =
            SwipeApi.retrofitService.getUserOnboardingDetails(userId, appToken)

        withContext(Dispatchers.Main) {
            if (isUserOnboarded(onboardingStatusDetails)) {
                Toast.makeText(activity,
                    activity.applicationContext.getString(R.string.please_complete_paysprint_kyc),
                    Toast.LENGTH_LONG).show()
                val intent = Intent(activity.applicationContext, HostActivity::class.java)
                intent.putExtra("pId", sharePrfeManager.partnerId)
                intent.putExtra("pApiKey", sharePrfeManager.partnerApiKey)
                intent.putExtra("mCode",
                    onboardingStatusDetails.data?.merchantId)
                intent.putExtra("mobile", sharePrfeManager.mGetMobile())
                intent.putExtra("lat", "42.10")
                intent.putExtra("lng", "76.00")
                intent.putExtra("firm", onboardingStatusDetails.data?.shopName)
                intent.putExtra("email", sharePrfeManager.email)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(activity, intent, 10, null)
            } else {
                Toast.makeText(activity, activity.applicationContext.getString(R.string.please_do_web_kyc_first), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun isUserOnboarded(onboardingStatusResponse: UserOnboardingDetailsResponse): Boolean =
    onboardingStatusResponse.message == "User Onboarded" && onboardingStatusResponse.status == "TXN"
