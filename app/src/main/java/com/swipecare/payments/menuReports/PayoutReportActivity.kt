package com.swipecare.payments.menuReports

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.adapters.PayoutTransactionsAdapter
import com.swipecare.payments.databinding.ActivityPayoutReportBinding

class PayoutReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayoutReportBinding
    private lateinit var sharePrfeManager: SharePrfeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payout_report)
        sharePrfeManager = SharePrfeManager.getInstance(this)

        setupActionbar()

        val viewModel: PayoutReportViewModel by viewModels()
        binding.lifecycleOwner = this

        viewModel.getPayoutReports(sharePrfeManager.mGetUserId(),
            sharePrfeManager.mGetappToken(),
            viewModel.start)

        viewModel.payoutReportResponse.observe(this) { payoutResponse ->
            binding.progressbar.visibility = View.GONE

            if (payoutResponse.bankTransactions?.size == 0) {
                binding.next.isEnabled = false
                return@observe
            }

            val adapter = PayoutTransactionsAdapter(resources)
            binding.bankTransactionsRecyclerview.adapter = adapter
            adapter.submitList(payoutResponse.bankTransactions)
        }

        binding.next.setOnClickListener {
            viewModel.onNextClick()
            if (viewModel.nextPage >= 0) {
                viewModel.getPayoutReports(sharePrfeManager.mGetUserId(),
                    sharePrfeManager.mGetappToken(),
                    viewModel.nextPage)
            }
        }

        binding.previous.setOnClickListener {
            viewModel.onPreviousClick()
            if (viewModel.prevPage >= 0) {
                viewModel.getPayoutReports(sharePrfeManager.mGetUserId(),
                    sharePrfeManager.mGetappToken(),
                    viewModel.prevPage)
            }
        }
    }

        private fun setupActionbar() {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = getString(R.string.payout_report)
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