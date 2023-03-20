package com.swipecare.payments.payout

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.swipecare.payments.Constants.PAYOUT_DETAILS
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.databinding.ActivitySwipePayoutBinding
import com.swipecare.payments.extenstions.toast
import com.swipecare.payments.model.payout.PayoutDetails

class SwipePayoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySwipePayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_swipe_payout)

        setupActionbar()

        binding.aepsBalance.text = SharePrfeManager.getInstance(this).mGetAEPSBalance()

        binding.submitButton.setOnClickListener {
            if (areAllEnteredInputFieldsValid()) {
                val payoutDetails = PayoutDetails(
                    customerName = binding.customerName.text.toString(),
                    customerMobile = binding.customerMobileNumber.text.toString(),
                    accountNumber = binding.accountNumber.text.toString(),
                    ifscCode = binding.ifscCode.text.toString(),
                    amount = binding.amount.text.toString()
                )

                val intent = Intent(this, SwipePayoutPaymentModeSelectionActivity::class.java)
                intent.putExtra(PAYOUT_DETAILS,payoutDetails)
                startActivity(intent)
            }
        }
    }

    private fun areAllEnteredInputFieldsValid(): Boolean {
        return when {
            binding.customerName.text.toString().length <= 0 -> {
                toast(R.string.please_enter_customer_name)
                binding.customerName.error = getString(R.string.please_enter_customer_name)
                binding.customerName.requestFocus()
                false
            }
            binding.customerMobileNumber.text.toString().length != 10 -> {
                toast(R.string.mobile_number_should_be_10_digits)
                binding.customerMobileNumber.error =
                    getString(R.string.mobile_number_should_be_10_digits)
                binding.customerMobileNumber.requestFocus()
                false
            }
            binding.accountNumber.text.toString().length <= 0 -> {
                toast(R.string.please_enter_account_number)
                binding.accountNumber.error = getString(R.string.please_enter_account_number)
                binding.accountNumber.requestFocus()
                false
            }
            binding.ifscCode.text.toString().length != 11 -> {
                toast(R.string.ifsc_code_11_digits)
                binding.ifscCode.error = getString(R.string.ifsc_code_11_digits)
                binding.ifscCode.requestFocus()
                false
            }
            binding.amount.text.toString().length == 0 -> {
                toast(R.string.please_enter_amount)
                binding.amount.error = getString(R.string.please_enter_amount)
                binding.amount.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun setupActionbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.account_details)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}