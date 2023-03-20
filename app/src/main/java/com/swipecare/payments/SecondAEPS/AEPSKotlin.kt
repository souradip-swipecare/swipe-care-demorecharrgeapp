package com.swipecare.payments.SecondAEPS

import android.Manifest
import android.app.AlertDialog
import android.app.KeyguardManager
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.swipecare.payments.*
import com.swipecare.payments.Constants.AEPS_BALANCE_ENQUIRY_RESPONSE
import com.swipecare.payments.Constants.REQUEST_CODE_SECURITY_SETTINGS
import com.swipecare.payments.da.PidData
import com.swipecare.payments.da.PidOptions
import com.swipecare.payments.databinding.ActivityAepsBinding
import com.swipecare.payments.model.AEPSBalanceEnquiryResponse
import com.swipecare.payments.model.bodypara.EnquiryBody
import com.swipecare.payments.network.retrofit.SwipeApi
import com.swipecare.payments.transactionReceipts.CashWithdrawAndBalanceEnquiryReceiptActivity
import com.swipecare.payments.utils.getErrInfo
import com.swipecare.payments.utils.getResponseJsonObject
import com.swipecare.payments.utils.isPidDataCaptureSuccessful
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.util.concurrent.Executor

class AEPSKotlin : AppCompatActivity() {
    var selectedService = "BE"
    private lateinit var mobile: String
    private lateinit var aadhaar: String
    private var amount: String = ""
    private var bank_code: String = ""
    private var bank_iin: String = ""
    private var pidData: PidData? = null
    private var pidDataString: String = ""
    private lateinit var dialog: ProgressDialog
    var selectedDevice = "mantra"

    private var mantra_rd_package_name = "com.mantra.rdservice"
    private var morpho_rd_package_name = "com.scl.rdservice"

    private var serializer: Serializer? = null

    //    to check if app is installed or not
    companion object {
        private const val FINGERPRINTDATAREQUEST = 0
        private const val DEVICEINFOREQUEST = 1
        private const val BIOMETRIC_ENROLLMENT_RESULT = 5

        fun isAppInstalled(context: Context, packageName: String): Boolean {
            return try {
                context.packageManager.getApplicationInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var biometricManager: BiometricManager
    private lateinit var promptInfoForAboveApi30: BiometricPrompt.PromptInfo
    private lateinit var promptInfoForBelowApi30: BiometricPrompt.PromptInfo
    private lateinit var cancellationSignal: CancellationSignal

    private lateinit var aepsBinding: ActivityAepsBinding
    private lateinit var coroutineScope: CoroutineScope
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aepsBinding = DataBindingUtil.setContentView(this, R.layout.activity_aeps)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.balance_enquiry)
        coroutineScope = CoroutineScope(Dispatchers.IO)

        // authentication
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence,
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    // when the user closes the biometric prompt
                    Toast.makeText(this@AEPSKotlin,
                        getString(R.string.please_try_again),
                        Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult,
                ) {
                    super.onAuthenticationSucceeded(result)
                    /*Toast.makeText(applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()*/
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    /*Toast.makeText(applicationContext, getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT)
                        .show()*/
                }
            })
        biometricManager = BiometricManager.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptInfoForAboveApi30 = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.enter_device_credentials_or_biometric))
                .setSubtitle(getString(R.string.unlock, getString(R.string.balance_enquiry)))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    biometricPrompt.authenticate(promptInfoForAboveApi30)
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    Toast.makeText(this,
                        getString(R.string.biometric_features_unavailable),
                        Toast.LENGTH_SHORT).show()
                    finish()
                    // TODO: fix this. when HW is not available we are not authenticating. so we have to fix this.
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    Toast.makeText(this,
                        getString(R.string.biometric_hardware_unavailable),
                        Toast.LENGTH_SHORT).show()
                    finish()
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    // Prompts the user to create credentials that your app accepts.
                    Toast.makeText(this,
                        getString(R.string.set_screen_lock_or_finger_print),
                        Toast.LENGTH_LONG).show()
                    try {
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                        startActivityForResult(enrollIntent,
                            BIOMETRIC_ENROLLMENT_RESULT)
                    } catch (e: ActivityNotFoundException) {
                        // devices below api 30
                        Toast.makeText(this,
                            getString(R.string.set_screen_lock_or_finger_print),
                            Toast.LENGTH_LONG).show()
                        gotoSecuritySettings(this)
                    } catch (e: java.lang.Exception) {
                        Toast.makeText(this, "error occurred:$e", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // for api below 30
            if (!checkBiometricSupport()) {
                // no biometric support on the device
            } else {
                promptInfoForBelowApi30 = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.enter_device_credentials_or_biometric))
                    .setSubtitle(getString(R.string.unlock, getString(R.string.balance_enquiry)))
                    .setDeviceCredentialAllowed(true)
                    .build()
                biometricPrompt.authenticate(promptInfoForBelowApi30)
            }
        }

/*        val keyguardManager: KeyguardManager =
            getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.isDeviceSecure*/
        // authentication end

        serializer = Persister()

        aepsBinding.radiogroupDevice.setOnCheckedChangeListener { _, checkedId ->
            selectedDevice = if (checkedId == R.id.radio_mantra) "mantra" else "morpho"
        }

        aepsBinding.edittextBankName.setOnClickListener {
            if (DetectConnection.checkInternetConnection(this)) {
                mGetBankList()
            } else {
                showNoInternetConnectionToast()
            }
        }

        /*aepsBinding.buttonCaptureDeviceInfo.setOnClickListener { v: View? ->
            if (DetectConnection.checkInternetConnection(this)) {
                if (selectedDevice == "mantra") {
                    if (isAppInstalled(this, mantra_rd_package_name)) {
                        mCaptureDeviceInfo(mantra_rd_package_name)
                    } else {
                        try {
                            startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$mantra_rd_package_name")))
                        } catch (anfe: ActivityNotFoundException) {
                            startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$mantra_rd_package_name")))
                        }
                    }
                } else {
                    if (isAppInstalled(this, morpho_rd_package_name)) {
                        mCaptureDeviceInfo(morpho_rd_package_name)
                    } else {
                        try {
                            startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$morpho_rd_package_name")))
                        } catch (anfe: ActivityNotFoundException) {
                            startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$morpho_rd_package_name")))
                        }
                    }
                }
            } else {
                showNoInternetConnectionToast()
            }
        }*/

        aepsBinding.scanFinger.setOnClickListener {
            if (DetectConnection.checkInternetConnection(this)) {
                if (aepsBinding.edittextNumber.text.toString() == "") {
                    Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT)
                        .show()
                } else if (aepsBinding.edittextNumber.text.toString().length < 10) {
                    Toast.makeText(this,
                        "Please enter a valid mobile number",
                        Toast.LENGTH_SHORT).show()
                } else if (aepsBinding.edittextAadharNumber.text.toString() == "") {
                    Toast.makeText(this, "Please enter aadhaar number", Toast.LENGTH_SHORT)
                        .show()
                } else if (aepsBinding.edittextAadharNumber.text.toString().length < 12) {
                    Toast.makeText(this,
                        "Please enter a valid aadhaar number",
                        Toast.LENGTH_SHORT).show()
                } else if (selectedService.equals("cw",
                        ignoreCase = true) && aepsBinding.edittextAmount.text.toString() == ""
                ) {
                    Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show()
                } else {
                    mobile = aepsBinding.edittextNumber.text.toString()
                    aadhaar = aepsBinding.edittextAadharNumber.text.toString()
                    amount = aepsBinding.edittextAmount.text.toString()
                    if (selectedDevice == "mantra") {
                        if (isAppInstalled(this, mantra_rd_package_name)) {
                            mGetBioData(mantra_rd_package_name)
                        } else {
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$mantra_rd_package_name")))
                            } catch (anfe: ActivityNotFoundException) {
                                startActivity(Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$mantra_rd_package_name")))
                            }
                        }
                    } else {
                        if (isAppInstalled(this, morpho_rd_package_name)) {
                            mGetBioData(morpho_rd_package_name)
                        } else {
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$morpho_rd_package_name")))
                            } catch (anfe: ActivityNotFoundException) {
                                startActivity(Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$morpho_rd_package_name")))
                            }
                        }
                    }
                }
            } else {
                showNoInternetConnectionToast()
            }
        }
        aepsBinding.submit.setOnClickListener {
            if (pidDataString == "") {
                Toast.makeText(this, "please scan your finger and submit", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val sharedPrefManager = SharePrfeManager.getInstance(this)
            // perform api call and send the data to the server
            dialog = ProgressDialog(this)
            dialog.setMessage("Fetching your balance. Please wait...")
            dialog.setCancelable(false)
            dialog.show()
            coroutineScope.launch {
                try {
                    val aepsBalanceEnquiryResponse = SwipeApi.retrofitService.balanceEnquiry(
                        EnquiryBody(
                            sharedPrefManager.mGetUserId(),
                            sharedPrefManager.mGetappToken(),
                            mobile,
                            aadhaar,
                            bank_iin,
                            pidDataString
                        )
                    )
                    withContext(Dispatchers.Main) {
                        dialog.dismiss()
                        pidDataString = ""
                        if (aepsBalanceEnquiryResponse.status == "false") {
                            Toast.makeText(this@AEPSKotlin,
                                aepsBalanceEnquiryResponse.message,
                                Toast.LENGTH_LONG).show()
                        } else {
                            showTransactionReceipt(aepsBalanceEnquiryResponse)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        dialog.dismiss()
                        pidDataString = ""
                        Toast.makeText(this@AEPSKotlin, "Error occurred: $e", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            checkBiometricSupport()
        } catch (e: Exception) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FINGERPRINTDATAREQUEST && resultCode == RESULT_OK) {
            try {
                if (data != null) {
                    pidDataString = data.getStringExtra("PID_DATA").toString()

                    val responseJsonObject = getResponseJsonObject(pidDataString)

                    if (isPidDataCaptureSuccessful(responseJsonObject)) {
                        Toast.makeText(this,
                            getString(R.string.fingerprint_scan_successful),
                            Toast.LENGTH_LONG).show()
                    } else {
                        pidDataString = ""

                        Toast.makeText(this,
                            getErrInfo(responseJsonObject),
                            Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: java.lang.Exception) {
                pidDataString = ""

                Toast.makeText(this,
                    getString(R.string.finger_print_scan_failed),
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showTransactionReceipt(aepsResponse: AEPSBalanceEnquiryResponse) {
        val intent = Intent(this, CashWithdrawAndBalanceEnquiryReceiptActivity::class.java)
        intent.putExtra(AEPS_BALANCE_ENQUIRY_RESPONSE,aepsResponse)
        intent.putExtra(Constants.MOBILE_NUMBER,aepsBinding.edittextNumber.text.toString())
        startActivity(intent)
        finish()
    }

    private fun mGetBankList() {
        val progressDialog: ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Fetching bank list, please wait...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        aepsBinding.edittextBankName.setClickable(false)
        val beneficiaryItems: MutableList<BankListItems>
        beneficiaryItems = ArrayList()
        val operatorsCardAdapter = BankListCardAdapter(this, beneficiaryItems)
        val sharePrfeManager = SharePrfeManager.getInstance(this)
        val stringRequest = StringRequest(Request.Method.GET,
            "https://urgentpe.com/api/android/aeps/banklist?user_id=" + sharePrfeManager.mGetUserId() + "&apptoken=" + sharePrfeManager.mGetappToken() + "&type=getbanks",
            { response ->
                progressDialog.dismiss()
                //Log.e("bank response", response)
                aepsBinding.edittextBankName.setClickable(true)
                if (response != "") {
                    var id = ""
                    var bank = ""
                    var bankiin = ""
                    val ifsc = ""
                    val bank_id = ""
                    var status = ""
                    var message = ""
                    try {
                        val jsonObject = JSONObject(response)
                        if (jsonObject.has("statuscode")) {
                            status = jsonObject.getString("statuscode")
                        }
                        if (jsonObject.has("message")) {
                            message = jsonObject.getString("message")
                        }
                        if (status.equals("txn", ignoreCase = true)) {
                            val jsonArray = jsonObject.getJSONArray("data")

                            //            if (jsonArray.length()!=0) {
                            for (i in 0 until jsonArray.length()) {
                                val data = jsonArray.getJSONObject(i)

                                //                if (activity_name.equalsIgnoreCase("money2"))
                                //                {
                                id = data.getString("id")
                                bank = data.getString("bankname")
                                bankiin = data.getString("bankiin")
                                val operators_items = BankListItems()
                                operators_items.id = id
                                operators_items.bank = bank
                                operators_items.bankiin = bankiin
                                beneficiaryItems.add(operators_items)
                                operatorsCardAdapter.notifyDataSetChanged()
                            }
                        } else if (status == "TXN") {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }
                        if (beneficiaryItems != null) {
                            val inflater2 =
                                this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            val v2 = inflater2.inflate(R.layout.custome_alert_bank_list, null)
                            val edittext_search = v2.findViewById<EditText>(R.id.edittext_search)
                            edittext_search.visibility = View.VISIBLE
                            val recyclerview_operator: RecyclerView =
                                v2.findViewById(R.id.recyclerview_operator)
                            val imageview_cancel = v2.findViewById<ImageView>(R.id.imageview_cancel)
                            recyclerview_operator.setHasFixedSize(true)
                            recyclerview_operator.layoutManager = LinearLayoutManager(this)
                            recyclerview_operator.adapter = operatorsCardAdapter
                            edittext_search.addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(
                                    charSequence: CharSequence,
                                    i: Int,
                                    i1: Int,
                                    i2: Int,
                                ) {
                                }

                                override fun onTextChanged(
                                    charSequence: CharSequence,
                                    i: Int,
                                    i1: Int,
                                    i2: Int,
                                ) {
                                }

                                override fun afterTextChanged(editable: Editable) {
                                    if (beneficiaryItems != null) {
                                        val temp: MutableList<BankListItems?> = ArrayList()
                                        for (d in beneficiaryItems) {
                                            //or use .equal(text) with you want equal match
                                            //use .toLowerCase() for better matches
                                            if (d.bank.toLowerCase().contains(editable.toString()
                                                    .toLowerCase()) || d.bankiin.toLowerCase()
                                                    .contains(editable.toString().toLowerCase())
                                            ) {
                                                temp.add(d)
                                            }
                                        }
                                        //update recyclerview
                                        operatorsCardAdapter.UpdateList(temp)
                                    }
                                }
                            })
                            val builder2 = AlertDialog.Builder(this)
                            builder2.setCancelable(false)
                            builder2.setView(v2)
                            AEPS.alertDialog_for_bank = builder2.create()
                            if (!AEPS.alertDialog_for_bank.isShowing) {
                                AEPS.alertDialog_for_bank.show()
                            }
                            imageview_cancel.setOnClickListener { AEPS.alertDialog_for_bank.dismiss() }
                        } else {
                            Toast.makeText(this, "Something wrong...", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            progressDialog.dismiss()
            aepsBinding.edittextBankName.isClickable = true
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    fun mGetBank(bank_id: String, bank: String?, bankIINNumber: String) {
        bank_code = bank_id
        bank_iin = bankIINNumber
        aepsBinding.edittextBankName.setText(bank)
    }


    private fun mCaptureDeviceInfo(packageName: String) {
        try {
            val intent = Intent()
            intent.setPackage(packageName)
            intent.action = "in.gov.uidai.rdservice.fp.INFO"
            startActivityForResult(intent, DEVICEINFOREQUEST)
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    }

    private fun mGetBioData(packageName: String) {
        try {
            val pIDOptions = PidOptions.getPIDOptions("")
            if (pIDOptions != null) {
                Log.d("PidOptions", pIDOptions)
                val intent = Intent()
                intent.setPackage(packageName)
                intent.action = "in.gov.uidai.rdservice.fp.CAPTURE"
                intent.putExtra("PID_OPTIONS", pIDOptions)
                startActivityForResult(intent, FINGERPRINTDATAREQUEST)
            }
        } catch (e: java.lang.Exception) {
            Log.e("Error", e.toString())
        }
    }

    private fun showNoInternetConnectionToast(message: String = "No internet connection") {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

    private fun gotoSecuritySettings(activity: AppCompatActivity) {
        try {
            val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
            activity.startActivityForResult(intent, REQUEST_CODE_SECURITY_SETTINGS)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "Error: $e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isDeviceSecure) {
            Toast.makeText(this,
                "Fingerprint authentication is not enabled in settings",
                Toast.LENGTH_SHORT).show()
            gotoSecuritySettings(this)
            return false
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this,
                "Fingerprint authentication permission not enabled",
                Toast.LENGTH_LONG).show()
            return false
        }
        return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }
}