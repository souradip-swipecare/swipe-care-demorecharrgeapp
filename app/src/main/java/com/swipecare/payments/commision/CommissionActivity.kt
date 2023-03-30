package com.swipecare.payments.commision


import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.databinding.ActivityCommissionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CommissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommissionBinding
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var pagerAdapter: CommissionPagerAdapter

    private var commissionTypesChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {}
    }

    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_commission)
        val toolbar: Toolbar = findViewById(R.id.toolbar_commission)

        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.commission)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.commission)

        val viewModel by viewModels<CommissionViewModel>()
        binding.lifecycleOwner = this

        coroutineScope = CoroutineScope(Dispatchers.Main)

        dialog = ProgressDialog(this)
        dialog.setMessage(getString(R.string.fetching_commission))

        val sharedPreferences = SharePrfeManager.getInstance(this)

        viewModel.showLoadingDialog(true)
        viewModel.getUserCommission(sharedPreferences.mGetappToken(),
            sharedPreferences.mGetUserId())

        viewModel.commissionDataJson.observe(this) { commissionDataJson ->
            viewModel.showLoadingDialog(false)

            val commission = commissionDataJson.myCommission.commission
            val commissionDataAsDictionary = commission.getCommissionDataAsDictionary()

            pagerAdapter = CommissionPagerAdapter(this, 2, commission)
            binding.commissionTypesPager.adapter = pagerAdapter
            binding.commissionTypesPager.registerOnPageChangeCallback(commissionTypesChangeCallback)
            TabLayoutMediator(binding.commissionTypes,
                binding.commissionTypesPager) { tab, position ->
                val keyByIndex = commissionDataAsDictionary.keys.elementAt(position)
                val valueOfElement = commissionDataAsDictionary.getValue(keyByIndex)
//                pagerAdapter.commission = valueOfElement!!
                tab.text = keyByIndex
            }.attach()
        }

        viewModel.showLoadingDialog.observe(this) {
            if (it) dialog.show() else dialog.dismiss()
        }
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