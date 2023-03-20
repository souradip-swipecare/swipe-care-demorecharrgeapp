package com.swipecare.payments.fund.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.swipecare.payments.DetectConnection
import com.swipecare.payments.MainActivity
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.databinding.BharatgatewayqrBinding
import com.swipecare.payments.fund.Bharatverify
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Bharatutrsubmit : AppCompatActivity(){
    private lateinit var binding: BharatgatewayqrBinding
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sharedPreferences: SharePrfeManager
    private var balance: String = ""
    private lateinit var textviewMessage: TextView
    private lateinit var rlMessage: RelativeLayout
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var qrcode: ImageView
    var utrn = ""
    var signature = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BharatgatewayqrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this@Bharatutrsubmit)
        progressDialog.setMessage("Processing your recharge.Please wait..")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        rlMessage = binding.rlMessage
        textviewMessage = binding.textviewMessage
        qrcode = binding.qrcode
        coroutineScope = CoroutineScope(Dispatchers.IO)
        sharedPreferences = SharePrfeManager.getInstance(this)
        val imageurl = intent.getStringExtra("qrImage").toString()
        val suburl = imageurl.substring(imageurl.indexOf(",") + 1)
        val qrImage = findViewById<ImageView>(R.id.qrcode)

        qrcode.setImageBitmap(decodePicString(suburl))


        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        username = sharedPreferences.getString("username", "") ?: ""
        password = sharedPreferences.getString("password", "") ?: ""
        balance = sharedPreferences.getString("balance", "") ?: ""
        binding.paybillnow.setOnClickListener {

            if (DetectConnection.checkInternetConnection(this)) {
                if (allInputFieldsAreValid()) {
                    initiatebharatTransfer()
                } else {
                    showErrorMessage()
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
                val profitresponse = SwipeApi.retrofitService.bharatverify(
                    Bharatverify(
                        sharedPreferences.mGetUserId(),
                        sharedPreferences.mGetappToken(),
                        utrn,
                        signature
                    )
                )
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if(profitresponse.status == "ERR") {
                        binding.textviewMessage.setText(profitresponse.message).toString()
                        showreponMessage()
                    }
                    if(profitresponse.status == "TXN"){
                        val intent = Intent(this@Bharatutrsubmit, MainActivity::class.java)

                    }
                    Toast.makeText(
                        this@Bharatutrsubmit,
                        "Something went wrong please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@Bharatutrsubmit, MainActivity::class.java)
                    startActivity(intent)



                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@Bharatutrsubmit,
                        "Error occurred: Something went wrong please try later or Same transaction allowed after 2 min",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun allInputFieldsAreValid(): Boolean {
        return when {


            /*binding.edittextEnterNumber.text.toString().length == 10 -> {
                // fetch the operator and circle

            }*/

            binding.utrno.text.toString().length < 10 -> {
                textviewMessage.text = getString(R.string.please_enter_a_valid_utr_no)
                showErrorMessage()
                false
            }
            binding.utrno.text.toString() == "" -> {
                textviewMessage.text = getString(R.string.please_enter_utr)
                showErrorMessage()
                false
            }
            else -> {
                utrn = binding.utrno.text.toString()
                signature = intent.getStringExtra("signature").toString()

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
    fun decodePicString(encodedString: String): Bitmap {
        val imageBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
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


