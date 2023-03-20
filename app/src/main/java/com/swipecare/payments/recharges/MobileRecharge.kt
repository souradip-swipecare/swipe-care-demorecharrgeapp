package com.swipecare.payments.recharges

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.swipecare.payments.*
import com.swipecare.payments.Operators.OperatorsFragment
import com.swipecare.payments.databinding.MobileRechargesBinding
import com.swipecare.payments.model.MobileRechargeResponse
import com.swipecare.payments.network.retrofit.SwipeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MobileRecharge : AppCompatActivity() {

    private lateinit var binding: MobileRechargesBinding
    private lateinit var mobileRechargeViewModel: MobileRechargeViewModel

    private lateinit var rlMessage: RelativeLayout

    private lateinit var username: String
    private lateinit var password: String
    private var balance: String = ""
    private var provider: String = ""

    private lateinit var progressDialog: ProgressDialog

    private val rechargePlanResult = 11
    private val rechargeOfferResult = 21

    var amount = ""
    var number: String? = null
    private lateinit var textviewMessage: TextView

    private lateinit var operatorName: String

    var icon = ""
    private lateinit var coroutineScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        binding =
            MobileRechargesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this@MobileRecharge)
        progressDialog.setMessage("Processing your recharge.Please wait..")

        rlMessage = binding.rlMessage
        textviewMessage = binding.textviewMessage
        coroutineScope = CoroutineScope(Dispatchers.IO)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.llDth.setOnClickListener {
            startActivity(Intent(this, DTH::class.java))
            this.overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            finish()
        }
        binding.llElectricity.setOnClickListener {
            startActivity(Intent(this, Electricity::class.java))
            this.overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            finish()
        }
        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        username = sharedPreferences.getString("username", "") ?: ""
        password = sharedPreferences.getString("password", "") ?: ""
        balance = sharedPreferences.getString("balance", "") ?: ""
        binding.imageviewContact.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intent, 10)
        }
        binding.llOperator.setOnClickListener {
            binding.edittextEnterNumber.clearFocus()
            hideKeyboard(this)

            val bottomSheetDialogFragment = OperatorsFragment()
            val bundle = Bundle()
            bundle.putString("type", "1")
            bundle.putString("activity", "mobile")
            bottomSheetDialogFragment.arguments = bundle
            supportFragmentManager.beginTransaction().add(R.id.content, bottomSheetDialogFragment)
                .addToBackStack(null).commit()
        }
        binding.seePlans.setOnClickListener {
            val mobileNumber = binding.edittextEnterNumber.text.toString()
            if (mobileNumber.length != 10) {
                Toast.makeText(this, "Mobile Number should be 10 digits", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, MobileRechargePlansActivity::class.java)
                intent.putExtra("mobileNumber", mobileNumber)
                startActivityForResult(intent, rechargePlanResult)
            }
        }
        binding.buttonPaynow.setOnClickListener {
            if (DetectConnection.checkInternetConnection(this)) {
                if (allInputFieldsAreValid()) {
                    mShowDialog()
                } else {
                    showErrorMessage()
                }
            } else {
                textviewMessage.text = getString(R.string.no_internet_connection)
                showErrorMessage()
            }
        }
        binding.seeOffers.setOnClickListener {
            if (DetectConnection.checkInternetConnection(this)) {
                if (mobileNumberInputIsValid()) {
                    val intent = Intent(this, RechargeOffersActivity::class.java)
                    intent.putExtra("mobileNumber", binding.edittextEnterNumber.text.toString())
                    startActivityForResult(intent, rechargeOfferResult)
                } else {
                    textviewMessage.text = getString(R.string.please_enter_a_valid_mobile_no)
                    showErrorMessage()
                }
            } else {
                textviewMessage.text = getString(R.string.no_internet_connection)
                showErrorMessage()
            }
        }
    }

    private fun mobileNumberInputIsValid(): Boolean {
        return binding.edittextEnterNumber.text.toString().length == 10
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            10 -> if (resultCode == RESULT_OK) {
                var cursor: Cursor? = null
                try {
                    var phoneNo: String? = null
                    val name: String? = null
                    val uri = data?.data
                    cursor = contentResolver.query(uri!!, null, null, null, null)
                    cursor!!.moveToFirst()
                    val phoneIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    phoneNo = cursor.getString(phoneIndex)
                    phoneNo = phoneNo.replace(" ".toRegex(), "")
                    if (phoneNo.startsWith("+91")) {
                        binding.edittextEnterNumber.setText(phoneNo.substring(3, 13))
                    } else {
                        binding.edittextEnterNumber.setText(phoneNo)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                cursor?.close()
            }
            12 -> if (resultCode == RESULT_OK) {
                val res = data?.extras
                val result = res!!.getString("qr_code")
                binding.edittextEnterNumber.setText(result)
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(
                    applicationContext,
                    "Unable to read QR Code.Please Try Again.",
                    Toast.LENGTH_LONG
                ).show()
            }
            rechargePlanResult -> {
                if (resultCode == RESULT_OK) {
                    val planAmount = data?.getStringExtra("planPrice")
                    if (planAmount?.isNotEmpty() == true) {
                        binding.edittextAmount.setText(planAmount)
                    }
                }
            }
            rechargeOfferResult -> {
                if (resultCode == RESULT_OK) {
                    val planAmount = data?.getStringExtra("offerPrice")
                    if (planAmount?.isNotEmpty() == true) binding.edittextAmount.setText(planAmount)
                }
            }
        }
    }

    private fun mShowDialog(message: String = "Please confirm mobile number, operator and amount. We cannot reverse Wrong Recharges under any circumstances.") {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Recharge for: ${binding.edittextEnterNumber.text}\nOperator: $operatorName\nAmount: \u20B9 $amount\nNote: $message")
            .setTitle("Recharge Confirmation")
        builder1.setCancelable(true)
            .setPositiveButton("Confirm") { dialog, i ->
                if (DetectConnection.checkInternetConnection(this)) {
                    val sharedPreferences = SharePrfeManager.getInstance(this)
                    val appToken = sharedPreferences.mGetappToken()
                    val userId = sharedPreferences.mGetUserId()
                    var mobileRechargeResponse: MobileRechargeResponse

                    coroutineScope.launch {
                        try {
                            withContext(Dispatchers.Main) {
                                progressDialog.show()
                                progressDialog.setCancelable(false)
                            }
                            mobileRechargeResponse = SwipeApi.retrofitService.doMobileRecharge(
                                appToken,
                                userId,
                                provider,
                                amount,
                                number!!
                            )
                            withContext(Dispatchers.Main) {
                                progressDialog.dismiss()
                                val intent =
                                    Intent(this@MobileRecharge, TransactionReciept::class.java)
                                intent.putExtra("status", mobileRechargeResponse.status)
                                if (mobileRechargeResponse.status == "ERR") {
                                    intent.putExtra("message", mobileRechargeResponse.description ?: "some error occurred.please try again")
                                } else {
                                    intent.putExtra("message", mobileRechargeResponse.status)
                                }
                                intent.putExtra("transactionid", mobileRechargeResponse.txnid ?: "")
                                intent.putExtra("transactionrrn", mobileRechargeResponse.refno ?: "")
                                intent.putExtra("transaction_type", "Mobile Recharge")
                                intent.putExtra(
                                    "operator",
                                    binding.textviewOperator.text.toString()
                                )
                                intent.putExtra("number", number)
                                intent.putExtra("price", amount)
                                startActivity(intent)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this@MobileRecharge,
                                    "Error occurred: Something went wrong please try later or Same transaction allowed after 2 min",
                                    Toast.LENGTH_SHORT
                                ).show()



                            }
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, id -> dialog.cancel() }
        val alert = builder1.create()
        alert.show()
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

    fun getData(provider_id: String, name: String) {
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