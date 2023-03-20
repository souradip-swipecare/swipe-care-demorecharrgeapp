package com.swipecare.payments.MoneyTransfer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.swipecare.payments.CallRestApiForMoneyTransfer;
import com.swipecare.payments.DetectConnection;
import com.swipecare.payments.R;
import com.swipecare.payments.SharePrfeManager;

import org.json.JSONException;
import org.json.JSONObject;

public class SenderActivity extends AppCompatActivity {
    EditText edittext_mobileno;
    LinearLayout ll_contain_fname_lname;
    EditText edittext_fname,edittext_register_benename,edittext_register_beneaccount,edittext_register_beneifsc;
    Button button_register_continue;

    TextView textview_balance;

    String mobile="",type="";
    ProgressDialog dialog;
    AlertDialog alertDialog;

    String status="";
    String message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_sender);

        textview_balance=findViewById(R.id.textview_balance);
        textview_balance.setText("Rs "+SharePrfeManager.getInstance(SenderActivity.this).mGetMainBalance());

        edittext_mobileno=findViewById(R.id.edittext_mobileno);
        ll_contain_fname_lname=findViewById(R.id.ll_contain_fname_lname);
        edittext_fname=findViewById(R.id.edittext_fname);
        edittext_register_benename=findViewById(R.id.edittext_register_benename);
        edittext_register_beneaccount=findViewById(R.id.edittext_register_beneaccount);
        edittext_register_beneifsc=findViewById(R.id.edittext_register_beneifsc);
        button_register_continue=findViewById(R.id.button_register_continue);
        edittext_mobileno.setText("");
        edittext_mobileno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>9) {
                    type="verification";
                    mobile=s.toString();
                    new CallMyRestApi().execute(SharePrfeManager.getInstance(SenderActivity.this).mGetBaseUrl()+"api/android/dmt/transaction?apptoken="+SharePrfeManager.getInstance(SenderActivity.this).mGetappToken()+"&user_id="+SharePrfeManager.getInstance(SenderActivity.this).mGetUserId()+"&type="+type+"&mobile="+mobile);
                }else{
                    ll_contain_fname_lname.setVisibility(View.GONE);
                }
            }
        });

        button_register_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(SenderActivity.this)) {
                    if (edittext_mobileno.getText().toString().equals("")) {
                        Toast.makeText(SenderActivity.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                    } else if (edittext_fname.getText().toString().equals("")) {
                        Toast.makeText(SenderActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
                    } else if (edittext_register_benename.getText().toString().equals("")) {
                        Toast.makeText(SenderActivity.this, "Please enter beneficiary name", Toast.LENGTH_SHORT).show();
                    } else if (edittext_register_beneaccount.getText().toString().equals("")) {
                        Toast.makeText(SenderActivity.this, "Please enter beneficiary account", Toast.LENGTH_SHORT).show();
                    } else if (edittext_register_beneifsc.getText().toString().equals("")) {
                        Toast.makeText(SenderActivity.this, "Please enter beneficiary ifsc", Toast.LENGTH_SHORT).show();
                    } else {
                        String name=edittext_fname.getText().toString();
                        String benename=edittext_register_benename.getText().toString();
                        String beneaccount=edittext_register_beneaccount.getText().toString();
                        String beneifsc=edittext_register_beneifsc.getText().toString();
                        type="registration";
                        new CallMyRestApi().execute(SharePrfeManager.getInstance(SenderActivity.this).mGetBaseUrl()+"api/android/dmt/transaction?apptoken="+SharePrfeManager.getInstance(SenderActivity.this).mGetappToken()+"&user_id="+SharePrfeManager.getInstance(SenderActivity.this).mGetUserId()+"&type="+type+"&mobile="+mobile+"&name="+name+"&benename="+benename+"&beneaccount="+beneaccount+"&beneifsc="+beneifsc);
                    }
                } else {
                    Toast.makeText(SenderActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    class CallMyRestApi extends CallRestApiForMoneyTransfer
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(SenderActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (type.equalsIgnoreCase("verification")) {
                mShowSenderCheckStatus(s);
            } else if (type.equalsIgnoreCase("registration")) {
                mShowRegistrationStatus(s);
            } else if (type.equalsIgnoreCase("generateOtp")) {
                mShowOtp(s);
            } else if (type.equalsIgnoreCase("customerverification")) {
                mShowRegistrationStatus(s);
            }
        }
    }

    protected void mShowSenderCheckStatus(final String json)
    {
        Log.e("data", json);
        String status="";
        String message="";
        try
        {
            JSONObject jsonObject=new JSONObject(json);
            if (jsonObject.has("status")) {
                status=jsonObject.getString("status");
            }else{
                status= "ERR";
            }

            if (jsonObject.has("message")) {
                message=jsonObject.getString("message");
            }else{
                message= "Technical Error";
            }

            if (status.equalsIgnoreCase("TXN")) {
                String name=jsonObject.getString("name");
                String totallimit=jsonObject.getString("totallimit");
                String usedlimit=jsonObject.getString("usedlimit");

                Bundle bundle=new Bundle();
                Intent intent=new Intent(SenderActivity.this, SenderDetailActivity.class);
                bundle.putString("sender_number",mobile);
                bundle.putString("name",name);
                bundle.putString("status",status);
                bundle.putString("available_limit",totallimit);
                bundle.putString("total_spend",usedlimit);
                bundle.putString("json",json);
                bundle.putInt("tab",1);
                intent.putExtras(bundle);
                startActivity(intent);
            } else if (status.equalsIgnoreCase("RNF")) {
                ll_contain_fname_lname.setVisibility(View.VISIBLE);
            } else if(status.equalsIgnoreCase("TXNOTP")) {
                type = "generateOtp";
                new CallMyRestApi().execute(SharePrfeManager.getInstance(SenderActivity.this).mGetBaseUrl()+"api/android/dmt/transaction?apptoken="+SharePrfeManager.getInstance(SenderActivity.this).mGetappToken()+"&user_id="+SharePrfeManager.getInstance(SenderActivity.this).mGetUserId()+"&type=generateOtp&mytype=CUSTVERIFICATION"+"&mobile="+mobile);
            }else{
                Toast.makeText(SenderActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void mShowRegistrationStatus(final String json)
    {
        try
        {
            JSONObject jsonObject=new JSONObject(json);
            if (jsonObject.has("statuscode")) {
                status=jsonObject.getString("statuscode");
            }

            if (jsonObject.has("message")) {
                message=jsonObject.getString("message");
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        if (status.equalsIgnoreCase("txn"))
        {
            Toast.makeText(SenderActivity.this, message, Toast.LENGTH_SHORT).show();
            type = "verification";
            new CallMyRestApi().execute(SharePrfeManager.getInstance(SenderActivity.this).mGetBaseUrl()+"api/android/dmt/transaction?apptoken="+SharePrfeManager.getInstance(SenderActivity.this).mGetappToken()+"&user_id="+SharePrfeManager.getInstance(SenderActivity.this).mGetUserId()+"&type=verification"+"&mobile="+mobile);
        } else {
            Toast.makeText(SenderActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    protected void mShowOtp(final String json){
        String status="";
        String message="";
        String myresponsecode = "";
        try {
            JSONObject jsonObject=new JSONObject(json);
            if (jsonObject.has("status")) {
                status=jsonObject.getString("status");
            }else{
                status= "ERR";
            }

            if (jsonObject.has("message")) {
                message = jsonObject.getString("message");
            }else{
                message = "Technical Error";
            }

            if (jsonObject.has("transid")) {
                myresponsecode=jsonObject.getString("transid");
            }else{
                myresponsecode= "none";
            }

            if (status.equalsIgnoreCase("txn")) {
                final EditText otpedittext = new EditText(SenderActivity.this);
                otpedittext.setSingleLine();
                otpedittext.setHint("Enter otp");
                FrameLayout container = new FrameLayout(SenderActivity.this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = 30;
                params.leftMargin = 50;
                params.rightMargin = 50;
                params.bottomMargin = 30;

                otpedittext.setLayoutParams(params);
                container.addView(otpedittext);
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(SenderActivity.this);

                final String finalMyresponsecode = myresponsecode;
                builder1.setCancelable(false)
                        .setTitle("Otp Verification")
                        .setView(container)
                        .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (DetectConnection.checkInternetConnection(SenderActivity.this)) {
                                    type = "customerverification";
                                    String otp = otpedittext.getText().toString();
                                    new CallMyRestApi().execute(SharePrfeManager.getInstance(SenderActivity.this).mGetBaseUrl() + "api/android/dmt/transaction?apptoken=" + SharePrfeManager.getInstance(SenderActivity.this).mGetappToken() + "&user_id=" + SharePrfeManager.getInstance(SenderActivity.this).mGetUserId() + "&type=" + type + "&mobile=" + mobile + "&responsecode=" + finalMyresponsecode + "&otp=" + otp);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(SenderActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder1.create();
                alert.show();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
