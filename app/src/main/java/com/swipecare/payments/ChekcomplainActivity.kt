package com.swipecare.payments

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.swipecare.payments.databinding.RechargeStaComplainBinding
import com.swipecare.payments.model.Recrecipt.Reciptforwd
import com.swipecare.payments.model.Statusres
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChekcomplainActivity : AppCompatActivity(){
    private lateinit var binding: RechargeStaComplainBinding
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var progressDialog: ProgressDialog
    private lateinit var rlMessage: RelativeLayout
    private lateinit var textviewMessage: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RechargeStaComplainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        coroutineScope = CoroutineScope(Dispatchers.IO)
        progressDialog = ProgressDialog(this@ChekcomplainActivity)
        progressDialog.setMessage("Processing your recharge status.Please wait..")
        rlMessage = binding.rlMessage
        textviewMessage = binding.textviewMessage


        binding.lottieAnimationView.playAnimation();
        binding.aadhar.text = intent.getStringExtra("textview_number")
        binding.balance.text = intent.getStringExtra("textview_provider")
        binding.rrn.text = intent.getStringExtra("textview_refno")
        binding.amount.text = intent.getStringExtra("textview_amount")
        binding.status.text = intent.getStringExtra("textview_status")
        binding.date.text = intent.getStringExtra("textview_date")
        binding.id.text = intent.getStringExtra("txnid")
        val status = intent.getStringExtra("textview_status")
//        Log.e("hhhhh", "onCreate: ${status}", )
        if (status == "success" && status != null){
            updatesuccess()
        }else if (status == "failed" && status != null){
            updatefailed()
        }else{
            updatepending()
        }
        binding.cardchksts.setOnClickListener{
            val sharedPreferences = SharePrfeManager.getInstance(this)
            val appToken = sharedPreferences.mGetappToken()
            val userId = sharedPreferences.mGetUserId()
            val refno = intent.getStringExtra("txnid").toString()
            var statusres : Statusres
            coroutineScope.launch {
                try {
                    withContext(Dispatchers.Main) {
                        progressDialog.show()
                        progressDialog.setCancelable(false)
                    }
                    statusres = SwipeApi.retrofitService.status(
                        userId,
                        appToken,
                        refno
                    )
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()

                        if (statusres.status == "ERR") {
                            textviewMessage.text = statusres.message
                            showErrorMessage()
                        } else if(statusres.status == "success"){
                            updatesuccess()
                            binding.status.text = statusres.status
                            binding.rrn.text = statusres.refno
                        } else if (statusres.status == "failed"){
                            updatefailed()
                            binding.status.text = statusres.status
                        }else if (statusres.status == "pending"){
                            updatepending()
                            binding.status.text = statusres.status
                            binding.rrn.text = statusres.refno
                        }else{
                            textviewMessage.text = getText(R.string.Somethinwentwrong)
                            showErrorMessage()
                        }


                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@ChekcomplainActivity,
                            "Error occurred: Something went wrong please try later or Same transaction allowed after 2 min",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun updatepending() {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.orange)
        }
        binding.toolbar4.background = getDrawable(R.drawable.pending_view)
        binding.lottieAnimationView.setAnimation(R.raw.pendinglotti)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(Constants.orange)))

    }
    private fun updatefailed() {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.red)
        }
        binding.toolbar4.background = getDrawable(R.drawable.cash_failed)
        binding.lottieAnimationView.setAnimation(R.raw.failedlotti)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(Constants.redColor)))

    }
    private fun updatesuccess() {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorGreenLight)
        }
        binding.toolbar4.background = getDrawable(R.drawable.cash_success)
        binding.lottieAnimationView.playAnimation()
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(Constants.colorGreenLight)))
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



}