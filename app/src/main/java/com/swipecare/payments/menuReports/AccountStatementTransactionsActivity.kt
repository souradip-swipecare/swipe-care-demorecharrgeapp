package com.swipecare.payments.menuReports

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.adapters.AccountStatementTransactionsAdapter
import com.swipecare.payments.databinding.ActivityAccountStatementTransactionsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class AccountStatementTransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountStatementTransactionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_account_statement_transactions)
        setupActionbar()
        val sharedPreferences = SharePrfeManager.getInstance(this)
        val viewModel by viewModels<AccountStatementTransactionsViewModel>()

        viewModel.getAccountStatementTransactions(sharedPreferences.mGetUserId(),
            sharedPreferences.mGetappToken())

        viewModel.accountStatementTransaction.observe(this) {
            binding.progressbar.visibility = View.GONE

            val adapter = AccountStatementTransactionsAdapter(resources)
            binding.accountStatementTransactionsRecycler.adapter = adapter
            adapter.submitList(it)
        }
    }

    private fun setupActionbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.main_wallet_report)
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