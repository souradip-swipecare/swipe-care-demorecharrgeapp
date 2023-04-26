package com.swipecare.payments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import com.swipecare.payments.activity.Bharatutrsubmit
import com.swipecare.payments.databinding.UseraddBinding
import com.swipecare.payments.model.Selectectrole

import com.swipecare.payments.network.retrofit.SwipeApi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Useraddd : AppCompatActivity() {
    private lateinit var binding: UseraddBinding

    private lateinit var coroutineScope: CoroutineScope
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sharedPreferences: SharePrfeManager
    private lateinit var textviewMessage: TextView
    private lateinit var rlMessage: RelativeLayout
    private var selectedPaymentMode: Selectectrole = Selectectrole.retailer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UseraddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this@Useraddd)
        progressDialog.setMessage("Please wait User add is progressing........")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)



        coroutineScope = CoroutineScope(Dispatchers.IO)
        sharedPreferences = SharePrfeManager.getInstance(this)

        rlMessage = binding.rlMessage
        textviewMessage = binding.textviewMessage


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.Useradd)
        val role = sharedPreferences.mGetRoleid()
        if (role == "4"){
            binding.linearLayout1.visibility = View.GONE
        }else if(role == "2"){
            binding.masterdris.visibility = View.GONE
            binding.retailer.setOnClickListener {
                selectedPaymentMode = Selectectrole.retailer
                setDristributor(R.color.payment_mode_unselected_background_color)
                setRetailer(R.color.payment_mode_selected_background_color)
            }
            binding.dristributor.setOnClickListener {
                selectedPaymentMode = Selectectrole.dristributor
                setDristributor(R.color.payment_mode_selected_background_color)
                setRetailer(R.color.payment_mode_unselected_background_color)
            }
        }else if (role == "3"){
            binding.masterdris.visibility = View.GONE
            binding.dristributor.visibility = View.GONE
            binding.retailer.visibility = View.GONE
            selectedPaymentMode = Selectectrole.retailer

        }else{
            binding.dristributor.setOnClickListener {
                selectedPaymentMode = Selectectrole.dristributor
                setDristributor(R.color.payment_mode_selected_background_color)
                setRetailer(R.color.payment_mode_unselected_background_color)
                setMaster(R.color.payment_mode_unselected_background_color)
            }
            binding.retailer.setOnClickListener {
                selectedPaymentMode = Selectectrole.retailer
                setDristributor(R.color.payment_mode_unselected_background_color)
                setRetailer(R.color.payment_mode_selected_background_color)
                setMaster(R.color.payment_mode_unselected_background_color)
            }
            binding.masterdris.setOnClickListener {
                selectedPaymentMode = Selectectrole.masterdristributor
                setDristributor(R.color.payment_mode_unselected_background_color)
                setRetailer(R.color.payment_mode_unselected_background_color)
                setMaster(R.color.payment_mode_selected_background_color)
            }


        }




        binding.addbutton.setOnClickListener {

            when (selectedPaymentMode) {
                Selectectrole.retailer -> {
                    retaileradd()
                }
                Selectectrole.dristributor -> {
                    drisadd()
                }
                Selectectrole.masterdristributor -> {
                    masteradd()
                }
            }
        }
    }

    private fun setRetailer(@ColorRes color: Int) {
        binding.retailer.setCardBackgroundColor(getColor(color))
    }

    private fun setDristributor(@ColorRes color: Int) {
        binding.dristributor.setCardBackgroundColor(getColor(color))
    }

    private fun setMaster(@ColorRes color: Int) {
        binding.masterdris.setCardBackgroundColor(getColor(color))
    }
    private fun retaileradd() {
        coroutineScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    progressDialog.show()
                    progressDialog.setCancelable(false)
                }

                val useraddres = SwipeApi.retrofitService.useradd(
                    sharedPreferences.mGetUserId(),
                    sharedPreferences.mGetappToken(),
                    binding.mobile.text.toString(),
                    binding.email.text.toString(),
                    binding.adress.text.toString(),
                    binding.city.text.toString(),
                    binding.state.text.toString(),
                    binding.pincode.text.toString(),
                    binding.pancard.text.toString(),
                    binding.addhaarcard.text.toString(),
//                    selction of role id
                    "4",
                    binding.name.text.toString(),
                )

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (useraddres.status == "ERR") {
                        binding.textviewMessage.setText(useraddres.message).toString()
                        showreponMessage()
                    }else if(useraddres.status == "TXN"){
                        binding.textviewMessage.setText(useraddres.message).toString()
                        showreponMessage()
                        val intent = Intent(this@Useraddd, Bharatutrsubmit::class.java)

                        startActivity(intent)

                    }
                    else{
                        binding.textviewMessage.setText("Something Went wrong").toString()
                        showreponMessage()
                    }

                }


//
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@Useraddd,
                        "Error occurred: Something went wrong please try later n",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun drisadd() {
        coroutineScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    progressDialog.show()
                    progressDialog.setCancelable(false)
                }

                val useraddres = SwipeApi.retrofitService.useradd(
                    sharedPreferences.mGetUserId(),
                    sharedPreferences.mGetappToken(),
                    binding.mobile.text.toString(),
                    binding.email.text.toString(),
                    binding.adress.text.toString(),
                    binding.city.text.toString(),
                    binding.state.text.toString(),
                    binding.pincode.text.toString(),
                    binding.pancard.text.toString(),
                    binding.addhaarcard.text.toString(),
//                    selction of role id
                    "3",

                    binding.name.text.toString(),
                )

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (useraddres.status == "ERR") {
                        binding.textviewMessage.setText(useraddres.message).toString()
                        showreponMessage()
                    }else if(useraddres.status == "TXN"){
                        binding.textviewMessage.setText(useraddres.message).toString()
                        showreponMessage()
                        val intent = Intent(this@Useraddd, Bharatutrsubmit::class.java)

                        startActivity(intent)

                    }
                    else{
                        binding.textviewMessage.setText("Something Went wrong").toString()
                        showreponMessage()
                    }

                }


//
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@Useraddd,
                        "Error occurred: Something went wrong please try later n",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun masteradd() {
        coroutineScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    progressDialog.show()
                    progressDialog.setCancelable(false)
                }

                val useraddres = SwipeApi.retrofitService.useradd(
                    sharedPreferences.mGetUserId(),
                    sharedPreferences.mGetappToken(),
                    binding.mobile.text.toString(),
                    binding.email.text.toString(),
                    binding.adress.text.toString(),
                    binding.city.text.toString(),
                    binding.state.text.toString(),
                    binding.pincode.text.toString(),
                    binding.pancard.text.toString(),
                    binding.addhaarcard.text.toString(),
//                    selction of role id
                    "2",

                    binding.name.text.toString(),
                )

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (useraddres.status == "ERR") {
                        binding.textviewMessage.setText(useraddres.message).toString()
                        showreponMessage()
                    }else if(useraddres.status == "TXN"){
                        binding.textviewMessage.setText(useraddres.message).toString()
                        showreponMessage()
                        val intent = Intent(this@Useraddd, Bharatutrsubmit::class.java)

                        startActivity(intent)

                    }
                    else{
                        binding.textviewMessage.setText("Something Went wrong").toString()
                        showreponMessage()
                    }

                }


//
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@Useraddd,
                        "Error occurred: Something went wrong please try later n",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun showreponMessage() {
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
}