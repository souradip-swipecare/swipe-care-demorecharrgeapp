package com.swipecare.payments

import android.Manifest
import android.app.AlertDialog
import android.app.KeyguardManager
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Parcelable
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.swipecare.payments.Constants.AEPS_MINI_STATEMENT_RESPONSE
import com.swipecare.payments.Constants.REQUEST_CODE_SECURITY_SETTINGS
import com.swipecare.payments.Constants.TRANSACTIONS
import com.swipecare.payments.SecondAEPS.AEPS
import com.swipecare.payments.SecondAEPS.AEPSKotlin
import com.swipecare.payments.SecondAEPS.BankListCardAdapterThree
import com.swipecare.payments.SecondAEPS.BankListItems
import com.swipecare.payments.da.PidOptions
import com.swipecare.payments.databinding.ActivityAepsMiniStatementBinding
import com.swipecare.payments.model.AEPSMiniStatementResponse
import com.swipecare.payments.model.bodypara.MinistBody
import com.swipecare.payments.network.retrofit.SwipeApiWithTimeoutObj
import com.swipecare.payments.transactionReceipts.MiniStatementReceiptActivity
import com.swipecare.payments.utils.getErrInfo
import com.swipecare.payments.utils.getResponseJsonObject
import com.swipecare.payments.utils.isPidDataCaptureSuccessful
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.Executor

class AEPSMiniStatementActivity : AppCompatActivity() {

    private lateinit var radiogroup_device: RadioGroup
    private lateinit var radio_mantra: RadioButton
    private lateinit var radio_morpho: RadioButton
    private var selected_device = "mantra"

    private lateinit var edittext_number: EditText
    private lateinit var edittext_aadhar_number: EditText
    private lateinit var edittext_amount: EditText
    private lateinit var edittext_bank_name: TextView
    private lateinit var scanButton: Button
    private lateinit var submitButton: Button

    private lateinit var dialog: ProgressDialog
    private lateinit var textview_response: TextView

    private var bank_iin = ""

    private var REQUEST = 0
    private var mantra_rd_package_name = "com.mantra.rdservice"
    private var morpho_rd_package_name = "com.scl.rdservice"

    companion object {
        lateinit var alertDialog_for_bank: AlertDialog
        private const val FINGERPRINTDATAREQUEST = 0
        private const val BIOMETRIC_ENROLLMENT_RESULT = 5
    }

    private lateinit var mobile: String
    private lateinit var aadhaar: String
    var biomatricData: String = ""
    var bank_code: String = ""

    var amount: String = ""

    var icon = ""
    private var pidDataString: String = ""

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var biometricManager: BiometricManager
    private lateinit var promptInfoForAboveApi30: PromptInfo
    private lateinit var promptInfoForBelowApi30: PromptInfo
    private lateinit var cancellationSignal: CancellationSignal

    private lateinit var binding: ActivityAepsMiniStatementBinding
    private lateinit var coroutineScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_aeps_mini_statement)
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
                    Toast.makeText(this@AEPSMiniStatementActivity,
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
                .setSubtitle(getString(R.string.unlock, getString(R.string.mini_statement)))
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
                        startActivityForResult(enrollIntent, BIOMETRIC_ENROLLMENT_RESULT)
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
                promptInfoForBelowApi30 = PromptInfo.Builder()
                    .setTitle(getString(R.string.enter_device_credentials_or_biometric))
                    .setSubtitle(getString(R.string.unlock, getString(R.string.mini_statement)))
                    .setDeviceCredentialAllowed(true)
                    .build()
                biometricPrompt.authenticate(promptInfoForBelowApi30)
            }
        }

        // authentication end
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.mini_statement)


        textview_response = binding.textviewResponse

        radiogroup_device = binding.radiogroupDevice
        radiogroup_device.setOnCheckedChangeListener { group, checkedId ->
            selected_device = if (checkedId == R.id.radio_mantra) {
                "mantra"
            } else {
                "morpho"
            }
        }
        radio_mantra = binding.radioMantra
        radio_morpho = binding.radioMorpho

        edittext_number = binding.edittextNumber
        edittext_aadhar_number = binding.edittextAadharNumber
        edittext_amount = binding.edittextAmount
        edittext_bank_name = binding.edittextBankName
        edittext_bank_name.setOnClickListener {
            if (DetectConnection.checkInternetConnection(this)) {
                mGetBankList()
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        scanButton = binding.scanFinger
        submitButton = binding.submit
        scanButton.setOnClickListener {
            if (DetectConnection.checkInternetConnection(this)) {
                if (allInputFieldsAreEntered()) {
                    mobile = edittext_number.text.toString()
                    aadhaar = edittext_aadhar_number.text.toString()
                    amount = edittext_amount.text.toString()
                    if (selected_device == "mantra") {
                        if (AEPSKotlin.isAppInstalled(this, mantra_rd_package_name)) {
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
                        if (AEPS.isAppInstalled(this, morpho_rd_package_name)) {
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
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
        submitButton.setOnClickListener {
            if (pidDataString == "") {
                Toast.makeText(this, "please scan your finger and submit", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val sharePrfeManager = SharePrfeManager.getInstance(this)
            if (allInputFieldsAreEntered()) {
                dialog = ProgressDialog(this)
                dialog.setMessage("Mini Statement fetching in progress. Please wait...")
                dialog.setCancelable(false)
                dialog.show()
                coroutineScope.launch {
                    try {
                        val miniStatementResponse =
                            SwipeApiWithTimeoutObj.retrofitService.miniStatement(
                                MinistBody(
                                    sharePrfeManager.mGetUserId(),
                                    sharePrfeManager.mGetappToken(),
                                    binding.edittextNumber.text.toString(),
                                    binding.edittextAadharNumber.text.toString(),
                                    bank_iin,
                                    pidDataString
                                )
                            )
                        withContext(Dispatchers.Main) {
                            dialog.dismiss()
                            pidDataString = ""
                            if (miniStatementResponse.status == "false") {
                                Toast.makeText(this@AEPSMiniStatementActivity,
                                    miniStatementResponse.message,
                                    Toast.LENGTH_LONG).show()
                            } else {
                                showTransactionReceipt(miniStatementResponse)
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            dialog.dismiss()
                            Toast.makeText(this@AEPSMiniStatementActivity,
                                "Error occurred: $e",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
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

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal.setOnCancelListener {
            Toast.makeText(this,
                "Authentication was cancelled by the user",
                Toast.LENGTH_SHORT).show()
        }
        return cancellationSignal
    }

    override fun onResume() {
        super.onResume()
        // TODO: In most cases we don't need this check since we are already checking in the MainActivity. But for safe side we are adding this check. so we can always remove this check in the future.
        try {
            checkBiometricSupport()
        } catch (e: Exception) {

        }
    }

    private fun showTransactionReceipt(aepsMiniStatementResponse: AEPSMiniStatementResponse) {
        val intent = Intent(this, MiniStatementReceiptActivity::class.java)
        intent.putExtra(AEPS_MINI_STATEMENT_RESPONSE,
            aepsMiniStatementResponse)
        startActivity(intent)
        finish()
    }


    fun mGetBankList() {
        val progressDialog: ProgressDialog = ProgressDialog(this)
        progressDialog.setMessage("Fetching bank list, please wait...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        edittext_bank_name.isClickable = false

        val beneficiaryItems: MutableList<BankListItems>
        beneficiaryItems = ArrayList()
        val operatorsCardAdapter = BankListCardAdapterThree(this, beneficiaryItems)
        val sharePrfeManager = SharePrfeManager.getInstance(this)
        val stringRequest = StringRequest(Request.Method.GET,
            "https://urgentpe.com/api/android/aeps/banklist?user_id=" + sharePrfeManager.mGetUserId() + "&apptoken=" + sharePrfeManager.mGetappToken() + "&type=getbanks",
            { response ->
                progressDialog.dismiss()
             //   Log.e("bank response", response)
                edittext_bank_name.isClickable = true
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
                                val operatorsItems = BankListItems()
                                operatorsItems.id = id
                                operatorsItems.bank = bank
                                operatorsItems.bankiin = bankiin
                                beneficiaryItems.add(operatorsItems)
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
                            val edittext_search =
                                v2.findViewById<EditText>(R.id.edittext_search)
                            edittext_search.visibility = View.VISIBLE
                            val recyclerview_operator: RecyclerView =
                                v2.findViewById(R.id.recyclerview_operator)
                            val imageview_cancel =
                                v2.findViewById<ImageView>(R.id.imageview_cancel)
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
                                        val temp: MutableList<BankListItems> = mutableListOf()
                                        for (d in beneficiaryItems) {
                                            //or use .equal(text) with you want equal match
                                            //use .toLowerCase() for better matches
                                            if (d.bank.toLowerCase()
                                                    .contains(editable.toString()
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
                            alertDialog_for_bank = builder2.create()
                            if (!alertDialog_for_bank.isShowing) {
                                alertDialog_for_bank.show()
                            }
                            imageview_cancel.setOnClickListener { alertDialog_for_bank.dismiss() }
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
            edittext_bank_name.isClickable = true
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FINGERPRINTDATAREQUEST && resultCode == RESULT_OK) {
            try {
                if (data != null) {
                    pidDataString = data.getStringExtra("PID_DATA").toString()

//                    Log.d("PID_DATA", pidDataString);


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
        /*if (requestCode == REQUEST && resultCode == RESULT_OK) {
            println("returning data " + data?.extras)
            val bundle = data?.extras
            if (bundle != null) {
                for (key in bundle.keySet()) {
                    val value = bundle[key] as Object
                    Log.d("returning", String.format("%s %s (%s)", key,
                        value.toString(), value.javaClass.name))
                    biomatricData = String.format("%s %s (%s)", key,
                        value.toString(), value.javaClass.name)
                    biomatricData =
                        biomatricData.replace("PID_DATA", "").replace("(java.lang.String)", "")

//                    to conver xml to JSON
                    var errCode = ""
                    var errInfo = ""
                    try {
                        val jsonObject = XML.toJSONObject(biomatricData)
                        Log.e("biodata in json", jsonObject.toString())
                        val jsonObject1 = jsonObject.getJSONObject("PidData")
                        val jsonObject2 = jsonObject1.getJSONObject("Resp")
                        errCode = jsonObject2.getString("errCode")
                        errInfo = jsonObject2.getString("errInfo")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    // to check data capture or not or device connected or not
                    val sharedPrefManager = SharePrfeManager.getInstance(this)
                    if (errCode.equals("0", ignoreCase = true)) {
//                        apptoken=&user_id=&mobileNumber=&adhaarNumber=&nationalBankIdentificationNumber=&biodata=&transactionAmount=
                        CallRestAPIForAEPSTranscation().execute("https://swipecare.co.in/api/android/aeps/CW?user_id=${sharedPrefManager.mGetUserId()}&apptoken=${sharedPrefManager.mGetappToken()}&mobileNumber=$mobile&adhaarNumber=$aadhaar&biodata=$biomatricData&nationalBankIdentificationNumber=$bank_iin&transactionAmount=$amount")
                    } else {
                        Toast.makeText(this, errInfo, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }*/
    }


    fun mGetBank(bank_id: String, bank: String?, bankIINNumber: String) {
        bank_code = bank_id
        edittext_bank_name.text = bank
        bank_iin = bankIINNumber
    }

    fun mGetBioData(package_name: String) {
        try {
            val pIDOptions = PidOptions.getPIDOptions("")
            if (pIDOptions != null) {
                Log.e("PidOptions", pIDOptions)
                val intent = Intent()
                intent.setPackage(package_name)
                intent.action = "in.gov.uidai.rdservice.fp.CAPTURE"
                intent.putExtra("PID_OPTIONS", pIDOptions)
                startActivityForResult(intent, FINGERPRINTDATAREQUEST)
            }
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    }

    private fun allInputFieldsAreEntered(): Boolean {
        return if (edittext_number.text.toString() == "") {
            Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT)
                .show()
            false
        } else if (edittext_number.text.toString().length < 10) {
            Toast.makeText(this,
                "Please enter a valid mobile number",
                Toast.LENGTH_SHORT).show()
            false
        } else if (edittext_aadhar_number.text.toString() == "") {
            Toast.makeText(this, "Please enter aadhaar number", Toast.LENGTH_SHORT)
                .show()
            false
        } else if (edittext_aadhar_number.text.toString().length < 12) {
            Toast.makeText(this,
                "Please enter a valid aadhaar number",
                Toast.LENGTH_SHORT).show()
            false
        } else {
            true
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