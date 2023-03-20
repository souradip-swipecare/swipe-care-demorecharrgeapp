package com.swipecare.payments.recharges

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.databinding.ActivityMobileRechargePlansBinding
import com.swipecare.payments.model.OperatorPlan
import com.swipecare.payments.repository.recharges.MobileRechargePlansRepository
import com.swipecare.payments.viewmodels.recharges.MobileRechargePlansViewModel
import com.swipecare.payments.viewmodels.recharges.MobileRechargePlansViewModelFactory
import kotlinx.coroutines.*


class MobileRechargePlansActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMobileRechargePlansBinding
    private lateinit var coroutineScope: CoroutineScope

    private lateinit var dialog: ProgressDialog
    private var planTypesChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {}
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile_recharge_plans)

        val mobileRechargePlansRepository = MobileRechargePlansRepository()
        val viewModelFactory = MobileRechargePlansViewModelFactory(mobileRechargePlansRepository)
        val viewModel: MobileRechargePlansViewModel by viewModels { viewModelFactory }

        binding.lifecycleOwner = this

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        coroutineScope = CoroutineScope(Dispatchers.Main)

        val sharedPrefs = SharePrfeManager.getInstance(this)

        dialog = ProgressDialog(this)
        dialog.setMessage(getString(R.string.fetching_operator))

        coroutineScope.launch {
            viewModel.showLoadingDialog(true)
            viewModel.getOperatorAndCircle(
                sharedPrefs.apiUserId,
                sharedPrefs.apiPassword,
                intent.getStringExtra("mobileNumber")!!
            )
        }
        // observers
        viewModel.operatorAndCircle.observe(this) { operatorAndCircle ->
            if (operatorAndCircle.mobile != null && operatorAndCircle.circle != null && operatorAndCircle.error == "0" && operatorAndCircle.status == "1") {
                // operator fetch successful
                if (operatorAndCircle.error == "0" && operatorAndCircle.status == "1") {
                    dialog.setMessage(getString(R.string.fetching_recharge_plans))
                    viewModel.showLoadingDialog(true)
                    GlobalScope.launch {
                        viewModel.getOperatorPlan(
                            sharedPrefs.apiUserId,
                            sharedPrefs.apiPassword,
                            operatorAndCircle!!
                        )
                    }
                }
            } else {
                viewModel.showLoadingDialog(false)
                Toast.makeText(this@MobileRechargePlansActivity,
                    "Can't fetch operator and plans ${operatorAndCircle.message}",
                    Toast.LENGTH_SHORT)
                    .show()
                binding.errorText.visibility = View.VISIBLE
            }
        }

        viewModel.operatorPlan.observe(this) { operatorPlan ->
            viewModel.showLoadingDialog(false)

            val adapter = MobileRechargePlansPagerAdapter(this,
                operatorPlan.Rdata.getTabTexts().size, this)
            adapter.setOperatorPlan(operatorPlan)
            adapter.setOperatorPlan(operatorPlan)
            binding.planTypesPager.adapter = adapter
            binding.planTypesPager.registerOnPageChangeCallback(planTypesChangeCallback)
            TabLayoutMediator(binding.planTypes, binding.planTypesPager) { tab, position ->
                tab.text = operatorPlan.Rdata.getTabTexts()[position]
            }.attach()
        }

        viewModel.showLoadingDialog.observe(this) {
            if (it) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }
    }

    fun setPrice(price: String) {
        val bundle = Bundle()
        bundle.putString("planPrice", price)
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

    override fun onDestroy() {
        super.onDestroy()
        binding.planTypesPager.unregisterOnPageChangeCallback(planTypesChangeCallback)
    }
}

class MobileRechargePlansPagerAdapter(
    activity: AppCompatActivity,
    private val itemsCount: Int,
    private val rechargePlansActivity: MobileRechargePlansActivity,
) :
    FragmentStateAdapter(activity) {

    private lateinit var operatorPlan: OperatorPlan

    fun setOperatorPlan(plan: OperatorPlan) {
        operatorPlan = plan
    }

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = RechargePlansFragment.getInstance()
        fragment.setActivity(rechargePlansActivity)

        val plantTypesList = operatorPlan.Rdata.getPlanTypesList()
        val bundle = Bundle()
        bundle.putParcelableArrayList("plans",
            plantTypesList[position] as ArrayList<out Parcelable>)
        fragment.arguments = bundle
        return fragment
    }
}




