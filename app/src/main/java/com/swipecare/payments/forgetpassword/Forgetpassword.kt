package com.swipecare.payments.forgetpassword

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.swipecare.payments.Login
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.databinding.LoginOtpPageBinding
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Forgetpassword : AppCompatActivity() {
    private lateinit var binding: LoginOtpPageBinding
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sharedPreferences: SharePrfeManager
    private lateinit var textviewMessage: TextView
    private lateinit var rlMessage: RelativeLayout
    var username =  ""
    var otp = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginOtpPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lottieAnimationView2.playAnimation()
        progressDialog = ProgressDialog(this@Forgetpassword)
        progressDialog.setMessage("Processing your recharge.Please wait..")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        rlMessage = binding.rlMessage
        textviewMessage = binding.textviewMessage
        coroutineScope = CoroutineScope(Dispatchers.IO)
        binding.buttonCancel.setOnClickListener {
             otp = binding.digit1.text.toString()
            if(binding.digit1.text.toString() == ""){
                Toast.makeText(this@Forgetpassword, "Please enter otp", Toast.LENGTH_SHORT).show()
            }else if(binding.digit1.text.toString().length < 6){
                textviewMessage.text = getString(R.string.please_enter_utr)
                showErrorMessage()
            }else{
                startforgetactivity()
            }
        }
    }

    private fun startforgetactivity() {
        username = intent.getStringExtra("username").toString()
        otp = binding.digit1.text.toString()
        coroutineScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    progressDialog.show()
                    progressDialog.setCancelable(false)
                }
                var otpsentforgt = SwipeApi.retrofitService.otpsentforgetsubmit(
                    username,
                    otp
                )
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if(otpsentforgt.status == "TXN"){
                        binding.textviewMessage.setText(otpsentforgt.message).toString()
                        showErrorMessage()
                    }else{
                        if(otpsentforgt.status == "ERR") {
                            binding.textviewMessage.setText(otpsentforgt.message).toString()
                            showErrorMessage()
                        }
                        else{
                            Toast.makeText(
                                this@Forgetpassword,
                                "Something went wrong please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    val intent = Intent(this@Forgetpassword, Login::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    // Log.e("errrrrr", "initiatebharatTransfer: ${e}", )
                    Toast.makeText(
                        this@Forgetpassword,
                        "Error occurred: ${e}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

}