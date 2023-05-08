package com.swipecare.payments

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.swipecare.payments.databinding.DownlineFundtransferBinding
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Fundtransfertodownline : AppCompatActivity() {
    private lateinit var binding: DownlineFundtransferBinding
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sharedPreferences: SharePrfeManager
    private lateinit var textviewMessage: TextView
    private var childid: String = ""
    var amount = ""
    var remark = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DownlineFundtransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this@Fundtransfertodownline)
        progressDialog.setMessage("Processing your Request.Please wait..")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val sharedPreferences = SharePrfeManager.getInstance(this)
        val appToken = sharedPreferences.mGetappToken()
        val userId = sharedPreferences.mGetUserId()
         textviewMessage = binding.textviewMessage
        binding.inputno.addTextChangedListener(object  : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }



            override fun afterTextChanged(s: Editable?) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.inputno.text.toString().length == 10){
                    coroutineScope.launch {
                        try {
                            withContext(Dispatchers.Main) {
                                progressDialog.show()
                                progressDialog.setCancelable(false)
                            }
                            val userfetch = SwipeApi.retrofitService.userbymobile(
                                appToken,
                                userId,
                                binding.inputno.text.toString()
                            )
                            withContext(Dispatchers.Main) {
                                progressDialog.dismiss()

                                when (userfetch.status) {
                                    "ERR" -> {
                                        textviewMessage.text = userfetch.message
                                        showErrorMessage()
                                    }
                                    "TXN" -> {
                                        binding.linearLayout23.visibility =View.VISIBLE
                                        binding.name.text = userfetch.name
                                        binding.walletbal.text = userfetch.wallet.toString()
                                        binding.userid.text = userfetch.userid.toString()
                                        childid = userfetch.userid.toString()
                                    }
                                    else -> {
                                        Toast.makeText(
                                            this@Fundtransfertodownline,
                                            "Error occurred: operator cannot fetch please try later or Same transaction allowed after 2 min",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this@Fundtransfertodownline,
                                    "Error occurred: Something went wrong please try later or Same transaction allowed after 2 min+"+e,
                                    Toast.LENGTH_SHORT
                                ).show()
                               // Log.d("bghnhnhn", "onTextChanged: "+e)



                            }
                        }
                    }
                }
            }

        })

        binding.transfer.setOnClickListener{
            if(allInputFieldsAreValid()){
                coroutineScope.launch {
                    try {
                        withContext(Dispatchers.Main) {
                            progressDialog.show()
                            progressDialog.setCancelable(false)
                        }
                       val fundtranres = SwipeApi.retrofitService.fundtransfertouser(
                            appToken,
                            userId,
                           childid,
                            amount,
                            remark,
                            "transfer"
                        )
                        withContext(Dispatchers.Main) {
                            progressDialog.dismiss()

                            if (fundtranres.status == "ERR") {
                                textviewMessage.text = fundtranres.message.toString()
                                showErrorMessage()
                            } else if(fundtranres.status == "TXN"){
                                showsuccessdialog()
                            } else {
                                textviewMessage.text = "Something went wrong try Again"
                                showErrorMessage()
                            }

                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            progressDialog.dismiss()
                            Toast.makeText(
                                this@Fundtransfertodownline,
                                "Error occurred: Something went wrong please try later or Same transaction allowed after 2 min",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else{
                    Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT)
                        .show()
            }
        }

    }

    private fun showErrorMessage() {
        binding.rlMessage.visibility = View.VISIBLE
        binding.rlMessage.postDelayed({
            val a = AnimationUtils.loadAnimation(
                this,
                R.anim.animation_down
            )
            binding.rlMessage.animation = a
            binding.rlMessage.clearAnimation()
            binding.rlMessage.visibility = View.GONE
        }, 3000)
    }
    private fun allInputFieldsAreValid(): Boolean {
        return when {
            binding.amount.text.toString() == "" -> {
                textviewMessage.text = getString(R.string.please_enter_a_valid_mobile_no)
                showErrorMessage()
                false
            }
            binding.amount.text.toString().length < 2 -> {
                textviewMessage.text = getString(R.string.please_enter_a_valid_mobile_no)
                showErrorMessage()
                false
            }
            binding.remark.text.toString() == "" -> {
                textviewMessage.text = getString(R.string.please_enter_remark)
                showErrorMessage()
                false
            }
            /*binding.edittextEnterNumber.text.toString().length == 10 -> {
                // fetch the operator and circle

            }*/

            else -> {
                amount = binding.amount.text.toString()
                remark = binding.remark.text.toString()
                true
            }
        }
    }
    private fun showsuccessdialog() {
        val dialogbinding = layoutInflater.inflate(R.layout.successdialog,null)
        val mydialog = Dialog(this)

        mydialog.setContentView(dialogbinding)
        val btn = dialogbinding.findViewById<Button>(R.id.btn)
        btn.setOnClickListener {
            mydialog.dismiss()
            val intent = Intent(this@Fundtransfertodownline, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        mydialog.show()
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