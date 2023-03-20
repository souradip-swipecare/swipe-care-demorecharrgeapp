package com.swipecare.payments.transactionReceipts

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.swipecare.payments.Constants.PAYOUT_DETAILS
import com.swipecare.payments.R
import com.swipecare.payments.databinding.ActivityReceiptPayoutBinding
import com.swipecare.payments.model.PayoutResponse

class ReceiptPayoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptPayoutBinding
    private var payoutResponse: PayoutResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_payout)
        payoutResponse = intent.getParcelableExtra(PAYOUT_DETAILS)

        payoutResponse?.let {
            binding.textviewTransactionId.text = payoutResponse?.id

            binding.textviewTransactionStatus.text = payoutResponse?.status
            binding.transactionStatus.visibility = View.VISIBLE

            binding.textviewTransactionMessage.text = payoutResponse?.message
            binding.transactionMessage.visibility = View.VISIBLE

            binding.textviewTransactionRrn.text = payoutResponse?.rrn
            binding.transactionRrnCard.visibility = View.VISIBLE

            binding.textviewTransactionAadhar.text = payoutResponse?.aadhar
            binding.transactionAadharCard.visibility = View.VISIBLE

            binding.textviewTransactionAmount.text = payoutResponse?.transactionAmount
            binding.transactionAmountCard.visibility = View.VISIBLE

            binding.textviewTransactionCreatedAt.text = payoutResponse?.createdAt
            binding.transactionCreatedAtCard.visibility = View.VISIBLE
        }
    }
}