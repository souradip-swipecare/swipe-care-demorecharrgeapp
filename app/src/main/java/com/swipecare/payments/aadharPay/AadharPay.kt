package com.swipecare.payments.aadharPay


import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.swipecare.payments.*
import com.swipecare.payments.SecondAEPS.AEPS
import com.swipecare.payments.SecondAEPS.BankListCardAdapterTwo
import com.swipecare.payments.SecondAEPS.BankListItems
import com.swipecare.payments.da.PidOptions
import org.json.JSONException
import org.json.JSONObject
import org.json.XML


class AadharPay : AppCompatActivity() {

    val selected_service = "M"

    private lateinit var radiogroup_device: RadioGroup
    private lateinit var radio_mantra: RadioButton
    private lateinit var radio_morpho: RadioButton
    var selected_device = "mantra"

    private lateinit var edittext_number: EditText
    private lateinit var edittext_aadhar_number: EditText
    private lateinit var edittext_amount: EditText
    private lateinit var edittext_bank_name: TextView


    private lateinit var button_scan_and_submit: Button

    public lateinit var dialog: ProgressDialog
    private lateinit var textview_response: TextView


    var REQUEST = 0

    var mantra_rd_package_name = "com.mantra.rdservice"
    var morpho_rd_package_name = "com.scl.rdservice"

    companion object {
        lateinit var alertDialog_for_bank: AlertDialog
    }

    private lateinit var mobile: String
    private lateinit var aadhaar: String
    var biomatricData: String = ""
    var bank_code: String = ""

    var amount: String = ""

    var icon = ""

//    private val CONFIG_ENVIRONMENT: String = PaisaNikalConfig.Config.ENVIRONMENT_PRODUCTION
//    private val CONFIG_AGENT_ID_CODE: String = SharePrfeManager.getInstance(this).mGetAepsid()
//    private var config: PaisaNikalConfiguration? = null
//    private var apiRequest: PaisaNikalRequest? = null
//    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aepswithdrawal)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        textview_response = findViewById(R.id.textview_response)

        radiogroup_device = findViewById(R.id.radiogroup_device)
        radiogroup_device.setOnCheckedChangeListener { group, checkedId ->
            selected_device = if (checkedId == R.id.radio_mantra) {
                "mantra"
            } else {
                "morpho"
            }
        }
        radio_mantra = findViewById(R.id.radio_mantra)
        radio_morpho = findViewById(R.id.radio_morpho)

        edittext_number = findViewById(R.id.edittext_number)
        edittext_aadhar_number = findViewById(R.id.edittext_aadhar_number)
        edittext_amount = findViewById(R.id.edittext_amount)
        edittext_bank_name = findViewById(R.id.edittext_bank_name)
        edittext_bank_name.setOnClickListener {
            if (DetectConnection.checkInternetConnection(this)) {
                mGetBankList()
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

//        button_scan_and_submit = findViewById(R.id.button_scan_and_submit)

        /*button_scan_and_submit.setOnClickListener {
            if (DetectConnection.checkInternetConnection(this)) {
                if (edittext_number.text.toString() == "") {
                    Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT)
                        .show()
                } else if (edittext_number.text.toString().length < 10) {
                    Toast.makeText(this,
                        "Please enter a valid mobile number",
                        Toast.LENGTH_SHORT).show()
                } else if (edittext_aadhar_number.text.toString() == "") {
                    Toast.makeText(this, "Please enter aadhaar number", Toast.LENGTH_SHORT)
                        .show()
                } else if (edittext_aadhar_number.text.toString().length < 12) {
                    Toast.makeText(this,
                        "Please enter a valid aadhaar number",
                        Toast.LENGTH_SHORT).show()
                } else if (selected_service.equals("cw",
                        ignoreCase = true) && edittext_amount.text.toString() == ""
                ) {
                    Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show()
                } else {
                    mobile = edittext_number.text.toString()
                    aadhaar = edittext_aadhar_number.text.toString()
                    amount = edittext_amount.text.toString()
                    if (selected_device == "mantra") {
                        if (AEPS.isAppInstalled(this, mantra_rd_package_name)) {
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
        }*/
    }

    inner class CallRestAPIForAEPSTranscation : CallRestApiForMoneyTransfer() {
        override fun onPreExecute() {
            super.onPreExecute()
            dialog = ProgressDialog(this@AadharPay)
            dialog.setMessage("Please wait...")
            dialog.setCancelable(false)
            dialog.show()
        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            dialog.dismiss()
            Toast.makeText(this@AadharPay, "response:$s", Toast.LENGTH_SHORT).show()
            Log.e("transaction response", s)
            var myresult = ""
            myresult = s
            if (myresult != "") {
                var statuscode = ""
                var message = ""
                var ackno = ""
                var balanceamount = ""
                var bankrrn = ""
                textview_response.text = s
                try {
                    val jsonObject = JSONObject(s)
                    if (jsonObject.has("statuscode")) {
                        statuscode = jsonObject.getString("statuscode")
                    }
                    if (jsonObject.has("message")) {
                        message = jsonObject.getString("message")
                    }
                    if (jsonObject.has("ackno")) {
                        ackno = jsonObject.getString("ackno")
                    }
                    if (jsonObject.has("balanceamount")) {
                        balanceamount = jsonObject.getString("balanceamount")
                    }
                    if (jsonObject.has("bankrrn")) {
                        bankrrn = jsonObject.getString("bankrrn")
                    }
                    if (statuscode != "") {
//                    Intent intent=new Intent(AEPS.this,Receipt.class);
//                    intent.putExtra("status",statuscode);
//                    intent.putExtra("message",message);
//                    intent.putExtra("amount",balanceamount);
//                    intent.putExtra("bankrrn",bankrrn);
//                    intent.putExtra("ackno",ackno);
//                    startActivity(intent);
//                    finish();
                        val intent = Intent(this@AadharPay, TransactionReciept::class.java)
                        intent.putExtra("status", statuscode)
                        intent.putExtra("message", message)
                        intent.putExtra("transactionid", ackno)
                        intent.putExtra("transaction_type", "AEPS")
                        intent.putExtra("operator", "AEPS Transaction")
                        intent.putExtra("number", mobile)
                        intent.putExtra("price", amount)
                        intent.putExtra("icon", icon)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@AadharPay,
                            "Something went wrong",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this@AadharPay, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    fun mGetBankList() {
        val progressDialog: ProgressDialog = ProgressDialog(this)
        progressDialog.setMessage("Fetching bank list, please wait...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        edittext_bank_name.isClickable = false

        val beneficiaryItems: MutableList<BankListItems>
        beneficiaryItems = ArrayList()
        val operatorsCardAdapter = BankListCardAdapterTwo(this, beneficiaryItems)
        val sharePrfeManager = SharePrfeManager.getInstance(this)
        val stringRequest = StringRequest(Request.Method.GET,
            "https://swipecare.co.in/api/android/aeps/banklist?user_id=" + sharePrfeManager.mGetUserId() + "&apptoken=" + sharePrfeManager.mGetappToken() + "&type=getbanks",
            { response ->
                progressDialog.dismiss()
                Log.e("bank response", response)
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
                                    i2: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    charSequence: CharSequence,
                                    i: Int,
                                    i1: Int,
                                    i2: Int
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

    fun mGetBank(bank_id: String, bank: String?) {
        bank_code = bank_id
        edittext_bank_name.text = bank
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
                startActivityForResult(intent, REQUEST)
            }
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    }

//        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//            super.onActivityResult(requestCode, resultCode, data)
//        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST && resultCode == RESULT_OK) {
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
                    // to check data capture or not or device connectted or not
                    val sharedPrefManager = SharePrfeManager.getInstance(this)
                    if (errCode.equals("0", ignoreCase = true)) {
                        CallRestAPIForAEPSTranscation().execute("https://swipecare.co.in/api/android/aeps/M?user_id=${sharedPrefManager.mGetUserId()}&apptoken=${sharedPrefManager.mGetappToken()}&mobile=$mobile&aadharNumber=$aadhaar&thumbdata=$biomatricData&nationalBankIdentificationNumber=$bank_code&transactionAmount=$amount")
                    } else {
                        Toast.makeText(this, errInfo, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //    for hit transaction api
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
