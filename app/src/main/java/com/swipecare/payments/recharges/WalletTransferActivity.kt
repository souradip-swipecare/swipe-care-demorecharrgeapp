package com.swipecare.payments.recharges

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.databinding.AepstransferBinding
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WalletTransferActivity : AppCompatActivity() {

    private lateinit var aepsTransferBinding: AepstransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aepsTransferBinding = DataBindingUtil.setContentView(this, R.layout.aepstransfer)
        val coroutineScope = CoroutineScope(Dispatchers.IO)

        val sharedPreferences = SharePrfeManager.getInstance(this)
        aepsTransferBinding.aepsbal.text = sharedPreferences.mGetAEPSBalance()

        aepsTransferBinding.buttonForSubmit.setOnClickListener {
            coroutineScope.launch {
                try {
                    val otpSentResponse = SwipeApi.retrofitService.walletTransfer(
                        sharedPreferences.mGetappToken(),
                        sharedPreferences.mGetUserId(),
                        aepsTransferBinding.amounttotransfer.text.toString().toDouble()
                    )
                    if (otpSentResponse.status == "otpsent") {
                        withContext(Dispatchers.Main) {
                            aepsTransferBinding.linearLayout3.visibility = View.VISIBLE
                            aepsTransferBinding.amounttotransfer.isEnabled = false
                        }
                        try {
                            val walletTransferStatus = SwipeApi.retrofitService.walletTransferEnterOtp(
                                sharedPreferences.mGetappToken(),
                                sharedPreferences.mGetUserId(),
                                aepsTransferBinding.amounttotransfer.text.toString().toDouble(),
                                getUserEnteredOtp()
                            )

                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@WalletTransferActivity,
                                    walletTransferStatus.status,
                                    Toast.LENGTH_LONG)
                                    .show()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@WalletTransferActivity,
                                    "Error Occurred: ${e.message}",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@WalletTransferActivity,
                                "Error Occurred",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@WalletTransferActivity,
                            "Error Occurred: ${e.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getUserEnteredOtp(): String {
        return aepsTransferBinding.otpNumber1.text.toString() +
                aepsTransferBinding.otpNumber2.text.toString() +
                aepsTransferBinding.otpNumber3.text.toString() +
                aepsTransferBinding.otpNumber4.text.toString() +
                aepsTransferBinding.otpNumber5.text.toString() +
                aepsTransferBinding.otpNumber6.text.toString()
    }
}