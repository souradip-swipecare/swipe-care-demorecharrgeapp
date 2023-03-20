package com.swipecare.payments.menuReports

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.adapters.AepsTransactionsAdapter
import com.swipecare.payments.databinding.ActivityAepsReportBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AepsReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAepsReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_aeps_report)
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val sharePrfeManager = SharePrfeManager.getInstance(this)
        setupActionbar()

        val viewModel: AepsReportViewModel by viewModels()

        viewModel.getAepsPayoutReport(
            sharePrfeManager.mGetUserId(),
            sharePrfeManager.mGetappToken()
        )

        viewModel.aepsReportResponse.observe(this) {
            binding.progressbar.visibility = View.GONE

            val adapter = AepsTransactionsAdapter(resources)
            binding.aepsTransactionReports.adapter = adapter
            adapter.submitList(it.data)
        }
    }

    private fun setupActionbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.aeps_report)
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