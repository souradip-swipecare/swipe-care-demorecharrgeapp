package com.swipecare.payments.payout

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.swipecare.payments.Constants.PAYOUT_DETAILS
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.databinding.ActivitySwipePayoutOtpBinding
import com.swipecare.payments.extenstions.toast
import com.swipecare.payments.model.PayoutResponse
import com.swipecare.payments.model.payout.OtpSentStatus
import com.swipecare.payments.model.payout.PaymentMode
import com.swipecare.payments.model.payout.PayoutDetails
import com.swipecare.payments.network.retrofit.SwipeApi
import com.swipecare.payments.transactionReceipts.ReceiptPayoutActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class SwipePayoutOtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySwipePayoutOtpBinding
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var sharedPreferences: SharePrfeManager
    private var payoutDetails: PayoutDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_swipe_payout_otp)
        coroutineScope = CoroutineScope(Dispatchers.IO)
        sharedPreferences = SharePrfeManager.getInstance(this)
        payoutDetails = intent?.getParcelableExtra<PayoutDetails>(PAYOUT_DETAILS)

        val mobileNumber = "******" + sharedPreferences.mGetMobile().subSequence(6, 10)
        val amount = payoutDetails?.amount
        binding.otpSentTextView.text = getString(R.string.otp_sent_text, mobileNumber, amount)

        binding.aepsBalance.text = sharedPreferences.mGetAEPSBalance()

        handleOtpEnter()

        binding.submitButton.setOnClickListener {
            val userEnteredOtp = getUserEnteredOtp()
            if (userEnteredOtp.length != 6) {
                toast(R.string.please_enter_otp)
            } else {
                coroutineScope.launch {
                    try {
                        payoutDetails?.let { payoutDetails ->
                            when (payoutDetails.paymentMode) {
                                PaymentMode.Imps -> {
                                    val payoutResponse =
                                        SwipeApi.retrofitService.impsMoneyTransferSendOtp(
                                            sharedPreferences.mGetUserId(),
                                            sharedPreferences.mGetappToken(),
                                            payoutDetails.customerName,
                                            payoutDetails.customerMobile,
                                            payoutDetails.accountNumber,
                                            payoutDetails.ifscCode,
                                            payoutDetails.amount,
                                            userEnteredOtp
                                        )
                                    showTransactionReceipt(payoutResponse)
                                }
                                PaymentMode.Rtgs -> {
                                    val payoutResponse =
                                        SwipeApi.retrofitService.impsMoneyTransferSendOtp(
                                            sharedPreferences.mGetUserId(),
                                            sharedPreferences.mGetappToken(),
                                            payoutDetails.customerName,
                                            payoutDetails.customerMobile,
                                            payoutDetails.accountNumber,
                                            payoutDetails.ifscCode,
                                            payoutDetails.amount,
                                            userEnteredOtp
                                        )
                                    showTransactionReceipt(payoutResponse)
                                }
                                PaymentMode.Neft -> {
                                    val payoutResponse =
                                        SwipeApi.retrofitService.impsMoneyTransferSendOtp(
                                            sharedPreferences.mGetUserId(),
                                            sharedPreferences.mGetappToken(),
                                            payoutDetails.customerName,
                                            payoutDetails.customerMobile,
                                            payoutDetails.accountNumber,
                                            payoutDetails.ifscCode,
                                            payoutDetails.amount,
                                            userEnteredOtp
                                        )
                                    showTransactionReceipt(payoutResponse)
                                }
                            }
                        }
                    } catch (e: HttpException) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext,
                                getString(R.string.something_went_wrong_sorry),
                                Toast.LENGTH_LONG)
                            showTransactionReceipt(
                                PayoutResponse(
                                    status = "Something Went Wrong",
                                    message = "Some error occurred on our end. we are sorry"
                                )
                            )
                        }
                    }
                }
            }
        }

        binding.resendOtp.setOnClickListener {
            initiateImpsTransfer()
        }
    }

    private fun initiateImpsTransfer() {
        coroutineScope.launch {
            try {
                val otpSentStatus = SwipeApi.retrofitService.impsMoneyTransfer(
                    sharedPreferences.mGetUserId(),
                    sharedPreferences.mGetappToken(),
                    payoutDetails?.customerName ?: "",
                    payoutDetails?.customerMobile ?: "",
                    payoutDetails?.accountNumber ?: "",
                    payoutDetails?.ifscCode ?: "",
                    payoutDetails?.amount ?: ""
                )
                if (isOtpSent(otpSentStatus)) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, getString(R.string.otp_resent), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: HttpException) {
                Log.e("PaymentModeError", "initiateImpsTransfer: ${e.message}")
            }
        }
    }

    private fun isOtpSent(otpSentStatus: OtpSentStatus) =
        otpSentStatus.status == getString(R.string.otp_sent)

    private fun handleOtpEnter() {
        binding.digit1.addTextChangedListener(GenericTextWatcher(binding.digit1, binding.digit2))
        binding.digit2.addTextChangedListener(GenericTextWatcher(binding.digit2, binding.digit3))
        binding.digit3.addTextChangedListener(GenericTextWatcher(binding.digit3, binding.digit4))
        binding.digit4.addTextChangedListener(GenericTextWatcher(binding.digit4, binding.digit5))
        binding.digit5.addTextChangedListener(GenericTextWatcher(binding.digit5, binding.digit6))
        binding.digit6.addTextChangedListener(GenericTextWatcher(binding.digit6, null))

        binding.digit1.setOnKeyListener(GenericKeyEvent(binding.digit1, null))
        binding.digit2.setOnKeyListener(GenericKeyEvent(binding.digit2, binding.digit1))
        binding.digit3.setOnKeyListener(GenericKeyEvent(binding.digit3, binding.digit2))
        binding.digit4.setOnKeyListener(GenericKeyEvent(binding.digit4, binding.digit3))
        binding.digit5.setOnKeyListener(GenericKeyEvent(binding.digit5, binding.digit4))
        binding.digit6.setOnKeyListener(GenericKeyEvent(binding.digit6, binding.digit5))
    }

    private suspend fun showTransactionReceipt(payoutResponse: PayoutResponse) {
        withContext(Dispatchers.Main) {
            val intent = Intent(applicationContext, ReceiptPayoutActivity::class.java)
            intent.putExtra(PAYOUT_DETAILS, payoutResponse)
            startActivity(intent)
        }
    }

    private fun getUserEnteredOtp(): String {
        val result = binding.digit1.text.toString() +
                binding.digit2.text.toString() +
                binding.digit3.text.toString() +
                binding.digit4.text.toString() +
                binding.digit5.text.toString() +
                binding.digit6.text.toString()
        return result
    }
}

class GenericKeyEvent internal constructor(
    private val currentView: EditText,
    private val previousView: EditText?,
) : View.OnKeyListener {
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.digit1 && currentView.text.isEmpty()) {
            //If current is empty then previous EditText's number will also be deleted
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }


}

// Taken from: https://stackoverflow.com/questions/38872546/edit-text-for-otp-with-each-letter-in-separate-positions
class GenericTextWatcher internal constructor(
    private val currentView: View,
    private val nextView: View?,
) :
    TextWatcher {
    override fun afterTextChanged(editable: Editable) {
        val text = editable.toString()
        when (currentView.id) {
            R.id.digit1 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.digit2 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.digit3 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.digit4 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.digit5 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.digit6 -> {}
        }
    }

    override fun beforeTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int,
    ) { // TODO Auto-generated method stub
    }

    override fun onTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int,
    ) { // TODO Auto-generated method stub
    }

}