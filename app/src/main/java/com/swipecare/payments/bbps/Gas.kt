package com.swipecare.payments.bbps

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.swipecare.payments.DetectConnection
import com.swipecare.payments.Operators.OperatorsFragment
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.databinding.GasbillPayBinding
import com.swipecare.payments.model.billlverify.Gas
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Gas : AppCompatActivity() {

    private lateinit var binding: GasbillPayBinding

    private lateinit var coroutineScope: CoroutineScope
    private lateinit var progressDialog: ProgressDialog
    private lateinit var operatorName: String
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
    var number: String? = null

    val cname: String? = null









    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GasbillPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this@Gas)
        progressDialog.setMessage("Processing your recharge.Please wait..")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        rlMessage = binding.rlMessage
        textviewMessage = binding.textviewMessage

        linearLayout17 = binding.linearLayout17
        coroutineScope = CoroutineScope(Dispatchers.IO)





        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        username = sharedPreferences.getString("username", "") ?: ""
        password = sharedPreferences.getString("password", "") ?: ""
        balance = sharedPreferences.getString("balance", "") ?: ""


        //operator get

        binding.llOperator.setOnClickListener {
//            binding.edittextEnterNumber.clearFocus()
            hideKeyboard(this)
            val bottomSheetDialogFragment = OperatorsFragment()
            val bundle = Bundle()
            bundle.putString("type", "4")
            bundle.putString("activity", "gass")
            bottomSheetDialogFragment.arguments = bundle
            supportFragmentManager.beginTransaction().add(R.id.content, bottomSheetDialogFragment)
                .addToBackStack(null).commit()
            linearLayout17.visibility = View.VISIBLE

        }
        /// get gas bill details
        binding.getgasdetails.setOnClickListener {
            if(mobileNumberInputIsValid()){


                 number = binding.edittextEnterNumber.toString()



                if (DetectConnection.checkInternetConnection(this)) {
                    val sharedPreferences = SharePrfeManager.getInstance(this)
                    val appToken = sharedPreferences.mGetappToken()
                    val userId = sharedPreferences.mGetUserId()
                    var gas: Gas

                    coroutineScope.launch {
                        try {
                            withContext(Dispatchers.Main) {
                                progressDialog.show()
                                progressDialog.setCancelable(false)
                            }
                            gas = SwipeApi.retrofitService.getgasbill(
                                appToken,
                                userId,
                                provider,
                                binding.edittextEnterNumber.text.toString()
                                )
                            withContext(Dispatchers.Main) {
                                progressDialog.dismiss()

                                if (gas.statuscode == "ERR"){
                                    Toast.makeText(
                                        this@Gas,
                                        "Something went wrong please try again later",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }else if (gas.statuscode == "TXN"){

                                    textviewMessage.text = gas.status
                                    showErrorMessage()
                                    false

                                    binding.cdetails.visibility = View.VISIBLE


                                    val planAmount = gas.amount
                                    if (planAmount?.isNotEmpty() == true) {
                                        binding.edittextAmount.setText(planAmount)
                                    }

                                    val coustomername = gas.name
                                    if (coustomername?.isNotEmpty() == true) {
                                        binding.cname.setText(coustomername)
                                    }else{
                                        Toast.makeText(
                                            this@Gas,
                                            "Something went wrong please try again later",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }







                                }
                                else{

                                    Toast.makeText(
                                        this@Gas,
                                        "Something went wrong please try again later",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

//                                intent.putExtra("status", gasbillDetails.status)
//                                if (gasbillDetails.status == "ERR") {
//                                    intent.putExtra("message", gasbillDetails.status ?: "some error occurred.please try again")
//                                } else {
//                                    intent.putExtra("message", gasbillDetails.status)
//                                }

                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this@Gas,
                                    "Error occurred: $e",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }else{
                    Toast.makeText(
                        this@Gas,
                        "Something went wrong please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }else{
                showErrorMessage()

            }


        }


//        binding.paybillnow.setOnClickListener {
//            if (DetectConnection.checkInternetConnection(this)) {
//                if (allInputFieldsAreValid()) {
//                    mShowDialog()
//                } else {
//                    showErrorMessage()
//                }
//            } else {
//                textviewMessage.text = getString(R.string.no_internet_connection)
//                showErrorMessage()
//            }
//        }

    }


//    private fun mShowDialog(message: String = "Please confirm mobile number, operator and amount. We cannot reverse Wrong Recharges under any circumstances.") {
//        val builder1 = AlertDialog.Builder(this)
//        builder1.setMessage("Paying for: ${binding.edittextEnterNumber.text}\nOperator: $operatorName\nAmount: \u20B9 $amount\nNote: $message")
//            .setTitle("GAS  Confirmation")
//        builder1.setCancelable(true)
//            .setPositiveButton("Confirm") { dialog, i ->
//                if (DetectConnection.checkInternetConnection(this)) {
//                    val sharedPreferences = SharePrfeManager.getInstance(this)
//                    val appToken = sharedPreferences.mGetappToken()
//                    val userId = sharedPreferences.mGetUserId()
//                    var gasbillDetails: GasbillDetails
//
//                    coroutineScope.launch {
//                        try {
//                            withContext(Dispatchers.Main) {
//                                progressDialog.show()
//                                progressDialog.setCancelable(false)
//                            }
//                            gasbillDetails = SwipeApi.retrofitService.paygasbill(
//                                appToken,
//                                userId,
//                                provider,
//                                amount,
//
//                                number!!
//                                        duedate
//
//                                        mobile
//
//                            )
//                            withContext(Dispatchers.Main) {
//                                progressDialog.dismiss()
//                                val intent =
//                                    Intent(this@Gas, TransactionReciept::class.java)
//                                intent.putExtra("status", gasbillDetails.status)
//                                if (gasbillDetails.status == "ERR") {
//                                    intent.putExtra("message", gasbillDetails.description ?: "some error occurred.please try again")
//                                } else {
//                                    intent.putExtra("message", gasbillDetails.status)
//                                }
//                                intent.putExtra("transactionid", gasbillDetails.txnid ?: "")
//                                intent.putExtra("transactionrrn", gasbillDetails.rrn ?: "")
//                                intent.putExtra("transaction_type", "Mobile Recharge")
//                                intent.putExtra(
//                                    "operator",
//                                    binding.textviewOperator.text.toString()
//                                )
//                                intent.putExtra("number", number)
//                                intent.putExtra("price", amount)
//                                startActivity(intent)
//                            }
//                        } catch (e: Exception) {
//                            withContext(Dispatchers.Main) {
//                                progressDialog.dismiss()
//                                Toast.makeText(
//                                    this@Gas,
//                                    "Error occurred: $e",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    }
//                } else {
//                    Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//            .setNegativeButton(
//                "Cancel"
//            ) { dialog, id -> dialog.cancel() }
//        val alert = builder1.create()
//        alert.show()
//    }







    //for gas bill details
    private fun mobileNumberInputIsValid(): Boolean {
        return  when{
            binding.edittextEnterNumber.text.toString() == ""  -> {
                run {
                    textviewMessage.text = getString(R.string.please_enter_mobile_number)
                    showErrorMessage()
                    false
                }
            }
            provider == "" -> run {
                textviewMessage.text = getString(R.string.please_select_operator)
                showErrorMessage()
                false
            }
            else -> {
                number = binding.edittextEnterNumber.toString()
                true

            }
        }
    }








    private fun allInputFieldsAreValid(): Boolean {
        return when {
            binding.edittextEnterNumber.text.toString() == "" -> {
                textviewMessage.text = getString(R.string.please_enter_mobile_number)
                showErrorMessage()
                false
            }
            binding.edittextEnterNumber.text.toString().length < 10 -> {
                textviewMessage.text = getString(R.string.please_enter_a_valid_mobile_no)
                showErrorMessage()
                false
            }
            /*binding.edittextEnterNumber.text.toString().length == 10 -> {
                // fetch the operator and circle

            }*/
            provider == "" -> {
                textviewMessage.text = getString(R.string.please_select_operator)
                showErrorMessage()
                false
            }
            binding.edittextAmount.text.toString() == "" -> {
                textviewMessage.text = getString(R.string.please_enter_amount)
                showErrorMessage()
                false
            }
            else -> {
                number = binding.edittextEnterNumber.text.toString()
                amount = binding.edittextAmount.text.toString()
                true
            }
        }
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






    fun mCloseFragment() {
        supportFragmentManager.popBackStack()
    }

    fun GetData(provider_id: String, name: String) {
        operatorName = name
        binding.textviewOperator.text = operatorName
        provider = provider_id
    }

    private fun hideKeyboard(activity: Activity) {
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0)
        } catch (e: NullPointerException) {
        }
    }





}