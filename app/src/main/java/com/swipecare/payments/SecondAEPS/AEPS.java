package com.swipecare.payments.SecondAEPS;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swipecare.payments.CallRestApiForMoneyTransfer;
import com.swipecare.payments.DetectConnection;


import com.swipecare.payments.R;
import com.swipecare.payments.SharePrfeManager;
import com.swipecare.payments.TransactionReciept;
import com.swipecare.payments.da.DeviceInfo;
import com.swipecare.payments.da.PidOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class AEPS extends AppCompatActivity {

    String selected_service = "BE";

    RadioGroup radiogroup_device;
    RadioButton radio_mantra, radio_morpho;
    String selected_device = "mantra";

    EditText edittext_number, edittext_aadhar_number, edittext_amount;
    TextView edittext_bank_name;

    Button buttonCaptureDeviceInfo, button_scan_and_submit;

    ProgressDialog dialog;

    int REQUEST = 0;
    int DEVICEINFOREQUEST = 1;

    String mantra_rd_package_name = "com.mantra.rdservice";
    String morpho_rd_package_name = "com.scl.rdservice";

    String mobile, aadhaar, biomatricData = "", bank_code = "", amount = "", bank_iin = "";

    public static AlertDialog alertDialog_for_bank;

    TextView textview_response;

    String icon = "";

    private Serializer serializer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        serializer = new Persister();

        textview_response = findViewById(R.id.textview_response);

        radiogroup_device = findViewById(R.id.radiogroup_device);
        radiogroup_device.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_mantra) {
                    selected_device = "mantra";
                } else {
                    selected_device = "morpho";
                }
            }
        });
        radio_mantra = findViewById(R.id.radio_mantra);
        radio_morpho = findViewById(R.id.radio_morpho);

        edittext_number = findViewById(R.id.edittext_number);
        edittext_aadhar_number = findViewById(R.id.edittext_aadhar_number);
        edittext_amount = findViewById(R.id.edittext_amount);

        edittext_bank_name = findViewById(R.id.edittext_bank_name);
        edittext_bank_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(AEPS.this)) {
                    mGetBankList();
                } else {
                    Toast.makeText(AEPS.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        buttonCaptureDeviceInfo = findViewById(R.id.button_capture_device_info);
//        buttonCaptureDeviceInfo.setOnClickListener(v -> {
//            if (DetectConnection.checkInternetConnection(AEPS.this)) {
//                if (selected_device.equals("mantra")) {
//                    if (isAppInstalled(AEPS.this, mantra_rd_package_name)) {
//                        mCaptureDeviceInfo(mantra_rd_package_name);
//                    } else {
//                        try {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mantra_rd_package_name)));
//                        } catch (android.content.ActivityNotFoundException anfe) {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mantra_rd_package_name)));
//                        }
//                    }
//                } else {
//                    if (isAppInstalled(AEPS.this, morpho_rd_package_name)) {
//                        mCaptureDeviceInfo(morpho_rd_package_name);
//                    } else {
//                        try {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + morpho_rd_package_name)));
//                        } catch (android.content.ActivityNotFoundException anfe) {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + morpho_rd_package_name)));
//                        }
//                    }
//                }
//            } else {
//                Toast.makeText(AEPS.this, "No internet connection", Toast.LENGTH_SHORT).show();
//            }
//        });

        /*button_scan_and_submit = findViewById(R.id.button_scan_and_submit);*/
        /*button_scan_and_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(AEPS.this)) {
                    if (edittext_number.getText().toString().equals("")) {
                        Toast.makeText(AEPS.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                    } else if (edittext_number.getText().toString().length() < 10) {
                        Toast.makeText(AEPS.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    } else if (edittext_aadhar_number.getText().toString().equals("")) {
                        Toast.makeText(AEPS.this, "Please enter aadhaar number", Toast.LENGTH_SHORT).show();
                    } else if (edittext_aadhar_number.getText().toString().length() < 12) {
                        Toast.makeText(AEPS.this, "Please enter a valid aadhaar number", Toast.LENGTH_SHORT).show();
                    } else if (selected_service.equalsIgnoreCase("cw") && edittext_amount.getText().toString().equals("")) {
                        Toast.makeText(AEPS.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                    } else {
                        mobile = edittext_number.getText().toString();
                        aadhaar = edittext_aadhar_number.getText().toString();
                        amount = edittext_amount.getText().toString();
                        if (selected_device.equals("mantra")) {
                            if (isAppInstalled(AEPS.this, mantra_rd_package_name)) {
                                mGetBioData(mantra_rd_package_name);
                            } else {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mantra_rd_package_name)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mantra_rd_package_name)));
                                }
                            }
                        } else {
                            if (isAppInstalled(AEPS.this, morpho_rd_package_name)) {
                                mGetBioData(morpho_rd_package_name);
                            } else {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + morpho_rd_package_name)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + morpho_rd_package_name)));
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(AEPS.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }


    //    for hit transaction api
    class CallRestAPIForAEPSTranscation extends CallRestApiForMoneyTransfer {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AEPS.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            Log.e("transaction response", s);

            String myresult = "";
            myresult = s;

            if (!myresult.equals("")) {
                String statuscode = "", message = "", ackno = "", balanceamount = "", bankrrn = "";
                textview_response.setText(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);

                    if (jsonObject.has("statuscode")) {
                        statuscode = jsonObject.getString("statuscode");
                    }
                    if (jsonObject.has("message")) {
                        message = jsonObject.getString("message");
                    }
                    if (jsonObject.has("ackno")) {
                        ackno = jsonObject.getString("ackno");
                    }
                    if (jsonObject.has("balanceamount")) {
                        balanceamount = jsonObject.getString("balanceamount");
                    }
                    if (jsonObject.has("bankrrn")) {
                        bankrrn = jsonObject.getString("bankrrn");
                    }

                    if (!statuscode.equals("")) {
//                    Intent intent=new Intent(AEPS.this,Receipt.class);
//                    intent.putExtra("status",statuscode);
//                    intent.putExtra("message",message);
//                    intent.putExtra("amount",balanceamount);
//                    intent.putExtra("bankrrn",bankrrn);
//                    intent.putExtra("ackno",ackno);
//                    startActivity(intent);
//                    finish();

                        Intent intent = new Intent(AEPS.this, TransactionReciept.class);
                        intent.putExtra("status", statuscode);
                        intent.putExtra("message", message);
                        intent.putExtra("transactionid", ackno);
                        intent.putExtra("transaction_type", "AEPS");
                        intent.putExtra("operator", "AEPS Transaction");
                        intent.putExtra("number", mobile);
                        intent.putExtra("price", amount);
                        intent.putExtra("icon", icon);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(AEPS.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(AEPS.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //  to rd service app request for fingureprint
    private final void mGetBioData(final String package_name) {
        try {
            String pIDOptions = PidOptions.getPIDOptions("");
            if (pIDOptions != null) {
                Log.e("PidOptions", pIDOptions);
                Intent intent = new Intent();
                intent.setPackage(package_name);
                intent.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
                intent.putExtra("PID_OPTIONS", pIDOptions);
                startActivityForResult(intent, REQUEST);
            }
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    private void mCaptureDeviceInfo(String package_name) {
        try {
            Intent intent = new Intent();
            intent.setPackage(package_name);
            intent.setAction("in.gov.uidai.rdservice.fp.INFO");
            startActivityForResult(intent, DEVICEINFOREQUEST);
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }


    //    to check app is install or not
    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //    to get finger scan result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DEVICEINFOREQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                String deviceInfo = data.getStringExtra("DEVICE_INFO");
                String rdServiceInfo = data.getStringExtra("RD_SERVICE_INFO");
                String display = "";
                if (rdServiceInfo != null) {
                    display = "RD Service Info :\n" + rdServiceInfo + "\n\n";
                    Log.i("InfoRDService", "onActivityResult: " + display);
                }
                if (deviceInfo != null) {
                    try {
                        DeviceInfo info = serializer.read(DeviceInfo.class, deviceInfo);
                        Log.i("additionalInfo", "onActivityResult: " + info.dc + info.add_info + info);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                                /*DeviceInfo info = serializer.read(DeviceInfo.class, result);
                                display = display + "Device Code: " + info.dc + "\n\n"
                                        + "Serial No: " + info.srno + "\n\n"
                                        + "dpId: " + info.dpId + "\n\n"
                                        + "MC: " + info.mc + "\n\n"
                                        + "MI: " + info.mi + "\n\n"
                                        + "rdsId: " + info.rdsId + "\n\n"
                                        + "rdsVer: " + info.rdsVer;*/
//                    display += "Device Info :\n" + deviceInfo;
                }
            }
        }
        if (requestCode == REQUEST && resultCode == RESULT_OK) {
            System.out.println("returning data " + data.getExtras());
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    Log.d("returning", String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));

                    biomatricData = String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName());
                    biomatricData = biomatricData.replace("PID_DATA", "").replace("(java.lang.String)", "");


//                    to conver mxl to JSON
                    String errCode = "";
                    String errInfo = "";
                    try {
                        JSONObject jsonObject = XML.toJSONObject(biomatricData);
                        Log.e("biodata in json", jsonObject.toString());
                        JSONObject jsonObject1 = jsonObject.getJSONObject("PidData");
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("Resp");
                        errCode = jsonObject2.getString("errCode");
                        errInfo = jsonObject2.getString("errInfo");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    to check data capture or not or device connected or not
                    SharePrfeManager sharedPrefManager = SharePrfeManager.getInstance(this);
                    if (errCode.equalsIgnoreCase("0")) {
                        new CallRestAPIForAEPSTranscation().execute(
                                "https://swipecare.co.in/api/android/aeps/BE?user_id=" + sharedPrefManager.mGetUserId() + "&apptoken=" + sharedPrefManager.mGetappToken() + "&mobileNumber=" + mobile + "&adhaarNumber=" + aadhaar + "&nationalBankIdentificationNumber=" + bank_iin + "&biodata=" + biomatricData, biomatricData);

                    } else {
                        Toast.makeText(this, errInfo, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    public void mGetBankList() {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(AEPS.this);
        progressDialog.setMessage("Fetching bank list, please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        edittext_bank_name.setClickable(false);

        final List<BankListItems> beneficiaryItems;

        beneficiaryItems = new ArrayList<>();
        final BankListCardAdapter operatorsCardAdapter = new BankListCardAdapter(AEPS.this, beneficiaryItems);
        SharePrfeManager sharePrfeManager = SharePrfeManager.getInstance(AEPS.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://swipecare.co.in/api/android/aeps/banklist?user_id=" + sharePrfeManager.mGetUserId() + "&apptoken=" + sharePrfeManager.mGetappToken() + "&type=getbanks",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        Log.e("bank response", response);
                        edittext_bank_name.setClickable(true);

                        if (!response.equals("")) {
                            String id = "";
                            String bank = "";
                            String bankiin = "";
                            String ifsc = "";
                            String bank_id = "";
                            String status = "";
                            String message = "";

                            try {

                                JSONObject jsonObject = new JSONObject(response);

                                if (jsonObject.has("statuscode")) {
                                    status = jsonObject.getString("statuscode");
                                }
                                if (jsonObject.has("message")) {
                                    message = jsonObject.getString("message");
                                }

                                if (status.equalsIgnoreCase("txn")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");

//            if (jsonArray.length()!=0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject data = jsonArray.getJSONObject(i);

//                if (activity_name.equalsIgnoreCase("money2"))
//                {
                                        id = data.getString("id");
                                        bank = data.getString("bankname");
                                        bankiin = data.getString("bankiin");

                                        BankListItems operators_items = new BankListItems();
                                        operators_items.setId(id);
                                        operators_items.setBank(bank);
                                        operators_items.setBankiin(bankiin);
                                        beneficiaryItems.add(operators_items);

                                        operatorsCardAdapter.notifyDataSetChanged();

                                    }
                                } else if (status.equals("TXN")) {
                                    Toast.makeText(AEPS.this, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AEPS.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }

                                if (beneficiaryItems != null) {
                                    LayoutInflater inflater2 = (LayoutInflater) AEPS.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v2 = inflater2.inflate(R.layout.custome_alert_bank_list, null);
                                    EditText edittext_search = v2.findViewById(R.id.edittext_search);
                                    edittext_search.setVisibility(View.VISIBLE);
                                    RecyclerView recyclerview_operator = v2.findViewById(R.id.recyclerview_operator);
                                    ImageView imageview_cancel = v2.findViewById(R.id.imageview_cancel);


                                    recyclerview_operator.setHasFixedSize(true);
                                    recyclerview_operator.setLayoutManager(new LinearLayoutManager(AEPS.this));
                                    recyclerview_operator.setAdapter(operatorsCardAdapter);

                                    edittext_search.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {

                                            if (beneficiaryItems != null) {
                                                List<BankListItems> temp = new ArrayList();
                                                for (BankListItems d : beneficiaryItems) {
                                                    //or use .equal(text) with you want equal match
                                                    //use .toLowerCase() for better matches
                                                    if (d.getBank().toLowerCase().contains(editable.toString().toLowerCase()) || d.getBankiin().toLowerCase().contains(editable.toString().toLowerCase())) {
                                                        temp.add(d);
                                                    }
                                                }
                                                //update recyclerview
                                                operatorsCardAdapter.UpdateList(temp);
                                            }
                                        }
                                    });

                                    final AlertDialog.Builder builder2 = new AlertDialog.Builder(AEPS.this);
                                    builder2.setCancelable(false);

                                    builder2.setView(v2);

                                    alertDialog_for_bank = builder2.create();

                                    if (!alertDialog_for_bank.isShowing()) {
                                        alertDialog_for_bank.show();
                                    }
                                    imageview_cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alertDialog_for_bank.dismiss();
                                        }
                                    });
                                } else {
                                    Toast.makeText(AEPS.this, "Something wrong...", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(AEPS.this, "Something wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                edittext_bank_name.setClickable(true);
                Toast.makeText(AEPS.this, "Something wrong", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(AEPS.this);
        queue.add(stringRequest);
    }

    public void mGetBank(String bank_id, String bank, String bankIINNumber) {
        bank_code = bank_id;
        bank_iin = bankIINNumber;
        edittext_bank_name.setText(bank);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

