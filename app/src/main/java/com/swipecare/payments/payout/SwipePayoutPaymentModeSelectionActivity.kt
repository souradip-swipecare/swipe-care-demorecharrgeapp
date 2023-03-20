package com.swipecare.payments.payout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.swipecare.payments.Constants.PAYOUT_DETAILS
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.databinding.ActivitySwipePayoutPaymentModeSelectionBinding
import com.swipecare.payments.extenstions.toast
import com.swipecare.payments.model.payout.OtpSentStatus
import com.swipecare.payments.model.payout.PaymentMode
import com.swipecare.payments.model.payout.PayoutDetails
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SwipePayoutPaymentModeSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySwipePayoutPaymentModeSelectionBinding
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var sharedPreferences: SharePrfeManager
    private lateinit var payoutDetails: PayoutDetails

    private var intentPayoutDetails: PayoutDetails? = null

    private var selectedPaymentMode: PaymentMode = PaymentMode.Rtgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_swipe_payout_payment_mode_selection)
        coroutineScope = CoroutineScope(Dispatchers.IO)
        intentPayoutDetails = intent.getParcelableExtra(PAYOUT_DETAILS)
        payoutDetails = PayoutDetails(
            customerName = intentPayoutDetails!!.customerName,
            customerMobile = intentPayoutDetails!!.customerMobile,
            accountNumber = intentPayoutDetails!!.accountNumber,
            ifscCode = intentPayoutDetails!!.ifscCode,
            amount = intentPayoutDetails!!.amount,
            paymentMode = selectedPaymentMode
        )

        sharedPreferences = SharePrfeManager.getInstance(this)

        binding.aepsBalance.text = sharedPreferences.mGetAEPSBalance()

        setupActionBar()

        fillInputFields()
        disableInputFields()

        binding.paymentModeImps.setOnClickListener {
            selectedPaymentMode = PaymentMode.Imps

            setImpsBackgroundColor(R.color.payment_mode_selected_background_color)

            setRtgsBackgroundColor(R.color.payment_mode_unselected_background_color)
            setNeftBackgroundColor(R.color.payment_mode_unselected_background_color)
        }

        binding.paymentModeRtgs.setOnClickListener {
            selectedPaymentMode = PaymentMode.Rtgs

            setRtgsBackgroundColor(R.color.payment_mode_selected_background_color)

            setImpsBackgroundColor(R.color.payment_mode_unselected_background_color)
            setNeftBackgroundColor(R.color.payment_mode_unselected_background_color)
        }

        binding.paymentModeNeft.setOnClickListener {
            selectedPaymentMode = PaymentMode.Neft

            setNeftBackgroundColor(R.color.payment_mode_selected_background_color)

            setImpsBackgroundColor(R.color.payment_mode_unselected_background_color)
            setRtgsBackgroundColor(R.color.payment_mode_unselected_background_color)
        }

        binding.submitButton.setOnClickListener {
            when (selectedPaymentMode) {
                PaymentMode.Neft -> {
                    initiateNeftTransfer()
                }
                PaymentMode.Rtgs -> {
                    initiateRtgsTransfer()
                }
                PaymentMode.Imps -> {
                    initiateImpsTransfer()
                }
            }
        }
    }

    private fun fillInputFields() {
        binding.accountNumber.setText(intentPayoutDetails?.accountNumber)
        binding.ifscCode.setText(intentPayoutDetails?.ifscCode)
        binding.amount.setText(intentPayoutDetails?.amount)
    }

    private fun disableInputFields() {
        binding.accountNumber.isEnabled = false
        binding.ifscCode.isEnabled = false
        binding.amount.isEnabled = false
    }

    private fun setNeftBackgroundColor(@ColorRes color: Int) {
        binding.paymentModeNeft.setCardBackgroundColor(getColor(color))
    }

    private fun setRtgsBackgroundColor(@ColorRes color: Int) {
        binding.paymentModeRtgs.setCardBackgroundColor(getColor(color))

    }

    private fun setImpsBackgroundColor(@ColorRes color: Int) {
        binding.paymentModeImps.setCardBackgroundColor(getColor(color))
    }

    private fun initiateNeftTransfer() {
        coroutineScope.launch {
            val otpSentStatus = SwipeApi.retrofitService.neftMoneyTransfer(
                sharedPreferences.mGetUserId(),
                sharedPreferences.mGetappToken(),
                payoutDetails.customerName,
                payoutDetails.customerMobile,
                payoutDetails.accountNumber,
                payoutDetails.ifscCode,
                payoutDetails.amount
            )
            if (isOtpSent(otpSentStatus)) {
                startSwipePayoutOtpActivity(selectedPaymentMode)
            }
        }
    }

    private fun initiateRtgsTransfer() {
        coroutineScope.launch {
            val otpSentStatus = SwipeApi.retrofitService.rtgsMoneyTransfer(
                sharedPreferences.mGetUserId(),
                sharedPreferences.mGetappToken(),
                payoutDetails.customerName,
                payoutDetails.customerMobile,
                payoutDetails.accountNumber,
                payoutDetails.ifscCode,
                payoutDetails.amount
            )
            if (isOtpSent(otpSentStatus)) {
                startSwipePayoutOtpActivity(selectedPaymentMode)
            }
        }
    }

    private fun initiateImpsTransfer() {
        coroutineScope.launch {
            try {
                val otpSentStatus = SwipeApi.retrofitService.impsMoneyTransfer(
                    sharedPreferences.mGetUserId(),
                    sharedPreferences.mGetappToken(),
                    payoutDetails.customerName,
                    payoutDetails.customerMobile,
                    payoutDetails.accountNumber,
                    payoutDetails.ifscCode,
                    payoutDetails.amount
                )
                if (isOtpSent(otpSentStatus)) {
                    startSwipePayoutOtpActivity(selectedPaymentMode)
                }
            } catch (e: HttpException) {
                Log.e("PaymentModeError", "initiateImpsTransfer: ${e.message}")
            }
        }
    }

    private fun isOtpSent(otpSentStatus: OtpSentStatus) =
        otpSentStatus.status == getString(R.string.otp_sent)

    private suspend fun startSwipePayoutOtpActivity(paymentMode: PaymentMode) {
        withContext(Dispatchers.Main) {
            val intent = Intent(this@SwipePayoutPaymentModeSelectionActivity,
                SwipePayoutOtpActivity::class.java)
            intent.putExtra(PAYOUT_DETAILS, payoutDetails.copy(paymentMode = paymentMode))
            startActivity(intent)
        }
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.verify_payout_details)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}