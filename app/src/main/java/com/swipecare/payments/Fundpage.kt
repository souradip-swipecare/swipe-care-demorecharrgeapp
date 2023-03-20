package com.swipecare.payments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.swipecare.payments.Constants.UPI_ADDMONEY
import com.swipecare.payments.Operators.OperatorsFragment
import com.swipecare.payments.databinding.GasbillPayBinding
import com.swipecare.payments.databinding.OnlineloadMitraBinding
import com.swipecare.payments.fund.activity.Bharatutrsubmit
import com.swipecare.payments.model.PayoutResponse
import com.swipecare.payments.model.Upimode
import com.swipecare.payments.model.billlverify.Gas
import com.swipecare.payments.model.payout.PaymentMode
import com.swipecare.payments.network.retrofit.SwipeApi
import com.swipecare.payments.profit.Profitresponse
import com.swipecare.payments.transactionReceipts.ReceiptPayoutActivity
import kotlinx.android.synthetic.main.mitralogin.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class Fundpage : AppCompatActivity() {

    private lateinit var binding: OnlineloadMitraBinding

    private lateinit var coroutineScope: CoroutineScope
    private lateinit var progressDialog: ProgressDialog
    private lateinit var operatorName: String
    private lateinit var sharedPreferences: SharePrfeManager
    private var selectedPaymentMode: Upimode = Upimode.bharat


    private var balance: String = ""
    private var provider: String = ""
    private lateinit var linearLayout17: LinearLayout
    private lateinit var cdetails: LinearLayout
    private lateinit var textviewMessage: TextView
    private lateinit var rlMessage: RelativeLayout
    private lateinit var getgasdetails: CardView

    private lateinit var username: String
    private lateinit var password: String
    var amount = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OnlineloadMitraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this@Fundpage)
        progressDialog.setMessage("Processing your recharge.Please wait..")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        rlMessage = binding.rlMessage
        textviewMessage = binding.textviewMessage

        coroutineScope = CoroutineScope(Dispatchers.IO)
        sharedPreferences = SharePrfeManager.getInstance(this)

        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        username = sharedPreferences.getString("username", "") ?: ""
        password = sharedPreferences.getString("password", "") ?: ""
        balance = sharedPreferences.getString("balance", "") ?: ""

        binding.mannualFundreqest.setOnClickListener {
            startActivity(Intent(this, WalletRequest::class.java))
            this.overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            finish()
        }
        binding.upigateway.setOnClickListener {
            selectedPaymentMode = Upimode.upi

            setImpsBackgroundColor(R.color.payment_mode_selected_background_color)
            setNeftBackgroundColor(R.color.payment_mode_unselected_background_color)
        }

        binding.mitragateway.setOnClickListener {
            selectedPaymentMode = Upimode.bharat

            setNeftBackgroundColor(R.color.payment_mode_selected_background_color)

            setImpsBackgroundColor(R.color.payment_mode_unselected_background_color)
        }

        binding.paybillnow.setOnClickListener {

            if (DetectConnection.checkInternetConnection(this)) {
                    when (selectedPaymentMode) {
                        Upimode.upi -> {
                            initiateupiTransfer()
                        }
                        Upimode.bharat -> {
                            initiatebharatTransfer()
                        }
                    }

            } else {
                textviewMessage.text = getString(R.string.no_internet_connection)
                showErrorMessage()
            }
        }
    }

    private fun initiatebharatTransfer() {
        coroutineScope.launch {
            try {
                val profitresponse = SwipeApi.retrofitService.addfundbharat(
                    sharedPreferences.mGetUserId(),
                    sharedPreferences.mGetappToken(),
                    binding.edittextamount.text.toString()
                )
                showTransactionReceipt(profitresponse)

//
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@Fundpage,
                        "Error occurred: Something went wrong please try later or Same transaction allowed after 2 min",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private suspend fun showTransactionReceipt(profitresponse : Profitresponse) {
        withContext(Dispatchers.Main) {
            if (profitresponse.status == "ERR") {
                binding.textviewMessage.setText(profitresponse.message).toString()
                showreponMessage()
            }
           // Log.e("res", "profitresponse.qrImage")
//            Toast.makeText(
//                this@Fundpage,profitresponse.qrImage,
//                Toast.LENGTH_SHORT
//            ).show()

            val intent = Intent(this@Fundpage, Bharatutrsubmit::class.java).also {
                it.putExtra("status", profitresponse.status )
                it.putExtra("qrImage", profitresponse.qrImage )
                it.putExtra("signature", profitresponse.signature )
                startActivity(it)

            }
        }
    }
    private fun initiateupiTransfer() {
        coroutineScope.launch {
            try {
                val addfund = SwipeApi.retrofitService.addfundupi(
                    sharedPreferences.mGetUserId(),
                    sharedPreferences.mGetappToken(),
                    amount
                )
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (addfund.status == "ERR") {
                        binding.textviewMessage.setText(addfund.message).toString()
                        showreponMessage()
                    }
                    val intent =
                        Intent(this@Fundpage, Bharatutrsubmit::class.java)
                    intent.putExtra("status", addfund.status)
//
                    intent.putExtra("url", addfund.message)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@Fundpage,
                        "Error occurred: Something went wrong please try later or Same transaction allowed after 2 min",
                        Toast.LENGTH_SHORT
                    ).show()



                }
            }
        }
    }

    private fun setNeftBackgroundColor(@ColorRes color: Int) {
        binding.mitragateway.setCardBackgroundColor(getColor(color))
    }


    private fun setImpsBackgroundColor(@ColorRes color: Int) {
        binding.upigateway.setCardBackgroundColor(getColor(color))
    }

    private fun allInputFieldsAreValid(): Boolean {
        return when {


            /*binding.edittextEnterNumber.text.toString().length == 10 -> {
                // fetch the operator and circle

            }*/

            binding.edittextamount.text.toString() < 10.toString() -> {
                textviewMessage.text = getString(R.string.please_enter_a_valid_mobile_no)
                showErrorMessage()
                false
            }
            binding.edittextamount.text.toString() == "" -> {
                textviewMessage.text = getString(R.string.please_enter_amount)
                showErrorMessage()
                false
            }
            else -> {
                amount = binding.edittextamount.text.toString()
                true
            }
        }
    }

    private fun showErrorMessage() {
        rlMessage.visibility = View.VISIBLE
        rlMessage.postDelayed({
            val a = AnimationUtils.loadAnimation(
                this,
                R.anim.animation_down
            )
            rlMessage.animation = a
            rlMessage.clearAnimation()
            rlMessage.visibility = View.GONE
        }, 3000)
    }
    private fun showreponMessage() {
        rlMessage.visibility = View.VISIBLE
        rlMessage.postDelayed({
            val a = AnimationUtils.loadAnimation(
                this,
                R.anim.animation_down
            )
            rlMessage.animation = a
            rlMessage.clearAnimation()
            rlMessage.visibility = View.GONE
        }, 3000)
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