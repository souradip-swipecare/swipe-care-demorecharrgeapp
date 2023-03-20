package com.swipecare.payments.recharges

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.adapters.RechargeOffersAdapter
import com.swipecare.payments.databinding.ActivityRechargeOffersBinding
import com.swipecare.payments.network.retrofit.PlanApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RechargeOffersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRechargeOffersBinding
    private lateinit var coroutineScope: CoroutineScope
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recharge_offers)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        coroutineScope = CoroutineScope(Dispatchers.IO)
        val sharePrfeManager = SharePrfeManager.getInstance(this)
        val mobileNumber = intent.getStringExtra("mobileNumber") ?: ""

        shouldShowProgressBar(true)
        coroutineScope.launch {
            try {
                val operatorAndCircle = PlanApi.planRetrofitService.getOperatorAndCircle(
                    sharePrfeManager.apiUserId,
                    sharePrfeManager.apiPassword,
                    mobileNumber
                )
                val rechargeOffers = PlanApi.planRetrofitService.getRechargeOffers(
                    sharePrfeManager.apiUserId,
                    sharePrfeManager.apiPassword,
                    mobileNumber,
                    operatorAndCircle.operatorCode ?: ""
                )
                withContext(Dispatchers.Main) {
                    shouldShowProgressBar(false)
                    if (rechargeOffers.error == "0" && rechargeOffers.status == "1" && rechargeOffers.message == "Offer Successfully Checked") {
                        val adapter =
                            RechargeOffersAdapter(RechargeOffersAdapter.OnclickListener { offer ->
                                val intent = Intent()
                                intent.putExtra("offerPrice", offer.price.toString())
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            })
                        binding.offersRecyclerView.adapter = adapter
                        adapter.submitList(rechargeOffers.offersData)
                    } else {
                        Toast.makeText(this@RechargeOffersActivity,
                            getString(R.string.offers_for_operators_available),
                            Toast.LENGTH_SHORT).show()
                        binding.offersRecyclerView.visibility = View.GONE
                        binding.offersForOperatorsNotAvailable.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    shouldShowProgressBar(false)
                    binding.offersRecyclerView.visibility = View.GONE
                    binding.offersForOperatorsNotAvailable.visibility = View.VISIBLE
                    Toast.makeText(this@RechargeOffersActivity,
                        "Error occurred: $e",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun shouldShowProgressBar(b: Boolean) {
        if (b) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
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