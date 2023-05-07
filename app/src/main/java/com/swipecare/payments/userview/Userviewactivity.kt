package com.swipecare.payments.userview

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.adapters.Userlistadapter
import com.swipecare.payments.databinding.ViewuserFragmentBinding

class Userviewactivity : AppCompatActivity() {
    private lateinit var binding : ViewuserFragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.viewuser_fragment)
        setupActionbar()
        val sharedPreferences = SharePrfeManager.getInstance(this)
        val viewModel by viewModels<Userviewmodel>()

        viewModel.getAccountStatementTransactions(sharedPreferences.mGetUserId(),
            sharedPreferences.mGetappToken())

        viewModel.userlistview.observe(this) {


            val adapter = Userlistadapter(resources)
            binding.viewuserfragment.adapter = adapter
            adapter.submitList(it.userslist)
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