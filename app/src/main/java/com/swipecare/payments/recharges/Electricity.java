package com.swipecare.payments.recharges;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.swipecare.payments.DetectConnection;
import com.swipecare.payments.Operators.OperatorsFragment;
import com.swipecare.payments.R;
import com.swipecare.payments.SharePrfeManager;
import com.swipecare.payments.TransactionReciept;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;


public class Electricity extends AppCompatActivity  {

    LinearLayout ll_operator;
    TextView textview_operator;

    EditText edittext_enter_number,edittext_mobile_number,edittext_biller,editText_bu;
    public static  EditText edittext_amount;
    Button button_verify,button_paynow;
    View editText_buview;

    RelativeLayout rl_due_date;
    TextView textview_due_date;

    ProgressDialog dialog;

    String username,password,balance,provider="",selected_due_date="",operator_name="",transaction_type="",biller_name="",mobile_number="",bu="";

    public String amount="",number;
    RelativeLayout rl_message;
    TextView textview_message;

    private int mYear, mMonth, mDay;

    LinearLayout ll_mobile,ll_dth;

    String icon="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    // Inflate the layout for this fragment
        setContentView(R.layout.electricity_recharge);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ll_mobile=findViewById(R.id.ll_mobile);
        ll_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Electricity.this,Mobile_Recharge.class));
                Electricity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        ll_dth=findViewById(R.id.ll_dth);
        ll_dth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Electricity.this,DTH.class));
                Electricity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        username=sharedPreferences.getString("username","");
        password=sharedPreferences.getString("password","");
        balance=sharedPreferences.getString("balance","");


        edittext_enter_number = findViewById(R.id.edittext_enter_number);
        edittext_mobile_number =  findViewById(R.id.edittext_mobile_number);
        edittext_biller =  findViewById(R.id.edittext_biller);
        edittext_amount = findViewById(R.id.edittext_amount);
        editText_bu = findViewById(R.id.edittext_bu);

        editText_buview = findViewById(R.id.edittext_buview);

        rl_due_date = findViewById(R.id.rl_due_date);
        textview_due_date = findViewById(R.id.textview_due_date);
        rl_due_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(Electricity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                 selected_due_date=  dayOfMonth+ "-" + (monthOfYear + 1)+ "-" +year;
                                textview_due_date.setText(selected_due_date);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });

        button_verify = findViewById(R.id.button_verify);
        button_paynow = findViewById(R.id.button_paynow);


        rl_message= findViewById(R.id.rl_message);
        textview_message= findViewById(R.id.textview_message);


        ll_operator= findViewById(R.id.ll_operator);
        textview_operator= findViewById(R.id.textview_operator);
        ll_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OperatorsFragment bottomSheetDialogFragment = new OperatorsFragment();
                Bundle bundle=new Bundle();
                bundle.putString("type","2");
                bundle.putString("activity","electricity");
                bottomSheetDialogFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.content,bottomSheetDialogFragment).addToBackStack(null).commit();
            }
        });


        button_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(Electricity.this))
                {
                    if (edittext_enter_number.getText().toString().equals(""))
                    {
                        textview_message.setText("Please enter electricity bill number");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                    else if (provider.equals(""))
                    {
                        textview_message.setText("Please select provider");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                    else if (edittext_mobile_number.getText().toString().equals(""))
                    {
                        textview_message.setText("Please enter mobile number");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                    else if (edittext_mobile_number.getText().toString().length()<10) {
                        textview_message.setText("Please enter a valid mobile number");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }else if (provider.equalsIgnoreCase("46") && editText_bu.getText().toString().equals("")) {
                        textview_message.setText("Please enter a valid BU");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                    else
                    {
                        number=edittext_enter_number.getText().toString();
                        mobile_number=edittext_mobile_number.getText().toString();
                        bu=editText_bu.getText().toString();
                        transaction_type="validate";
                        mElectricityBillPay(SharePrfeManager.getInstance(Electricity.this).mGetappToken(),
                                SharePrfeManager.getInstance(Electricity.this).mGetUserId(),provider,
                                amount,number,transaction_type,biller_name,mobile_number,selected_due_date,bu);
                    }
                }
                else
                {
                    textview_message.setText("No Internet Connection");
                    rl_message.setVisibility(View.VISIBLE);
                    rl_message.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                            rl_message.setAnimation(a);
                            rl_message.clearAnimation();
                            rl_message.setVisibility(View.GONE);
                        }
                    }, 3000);
                }
            }
        });

        button_paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (DetectConnection.checkInternetConnection(Electricity.this)) {
                    if (edittext_enter_number.getText().toString().equals("")) {
                        textview_message.setText("Please enter electricity bill number");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                    else if (provider.equals("")) {
                        textview_message.setText("Please select operator");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                    else if (edittext_mobile_number.getText().toString().equals("")) {
                        textview_message.setText("Please enter mobile number");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                    else if (edittext_mobile_number.getText().toString().length()<10) {
                        textview_message.setText("Please enter a valid mobile number");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                    else if (edittext_biller.getText().toString().equals("")) {
                        textview_message.setText("Please enter biller number");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                    else if (selected_due_date.equals("")) {
                        textview_message.setText("Please select due date");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                    else if (edittext_amount.getText().toString().equals("")) {
                        textview_message.setText("Please enter amount");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                    else {
                        transaction_type="payment";
                        biller_name=edittext_biller.getText().toString().replaceAll(" ","%20");
                        mobile_number=edittext_mobile_number.getText().toString();
                        number = edittext_enter_number.getText().toString();

                        amount = edittext_amount.getText().toString();
                        if (amount.contains("."))
                        {
                             amount=amount.substring(0,amount.indexOf("."));
                        }
                        mShowDialog("Please confirm operator, number and amount, wrong Bill payment will not reverse at any circumstances.");
                    }
                }
                else
                {
                    textview_message.setText("No Internet Connection");
                    rl_message.setVisibility(View.VISIBLE);
                    rl_message.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation a = AnimationUtils.loadAnimation(Electricity.this, R.anim.animation_down);
                            rl_message.setAnimation(a);
                            rl_message.clearAnimation();
                            rl_message.setVisibility(View.GONE);
                        }
                    }, 3000);
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

    private  void mElectricityBillPay(final  String appToken, final String user_id, final String provider, final String amount, final String number, final String type, final String biller, final String mobile, final String due_date, final String bu) {
        class getJSONData extends AsyncTask<String, String, String> {
            HttpURLConnection urlConnection;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog=new ProgressDialog(Electricity.this);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
            }
            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    Log.e("url", SharePrfeManager.getInstance(Electricity.this).mGetBaseUrl()+"api/android/billpay/transaction?apptoken="+appToken+"&user_id="+user_id+"&type="+type+"&provider_id="+provider+"&number="+number+"&mobile="+mobile+"&amount="+amount+"&biller="+biller+"&duedate="+due_date+"&bu="+bu);
                    URL url = new URL(SharePrfeManager.getInstance(Electricity.this).mGetBaseUrl()+"api/android/billpay/transaction?apptoken="+appToken+"&user_id="+user_id+"&type="+type+"&provider_id="+provider+"&number="+number+"&mobile="+mobile+"&amount="+amount+"&biller="+biller+"&duedate="+due_date+"&bu="+bu);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
                return result.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                dialog.dismiss();
                if (transaction_type.equalsIgnoreCase("validate"))
                {
                    mVerifyShowResponse(result);
                }
                else {
                    Showdata(result);
                }
            }

        }

        getJSONData getJSONData=new getJSONData();
        getJSONData.execute();
    }

    public void GetData(String provider_id,String name)
    {
        operator_name=name;
        textview_operator.setText(operator_name);
        provider=provider_id;
        if(provider.equalsIgnoreCase("46")){
            editText_bu.setVisibility(View.VISIBLE);
            editText_buview.setVisibility(View.VISIBLE);
        }else {
            editText_bu.setVisibility(View.GONE);
            editText_buview.setVisibility(View.GONE);
        }
    }


    protected void Showdata(final String json)
    {
        Log.e("data", json);
        String status="";
        String message="";
        String txnid="";
        String rrn="";
        try {
            JSONObject jsonObject = new JSONObject(json);
            status=jsonObject.getString("status");
            message=jsonObject.getString("message");
            if (jsonObject.has("txnid")){
                txnid=jsonObject.getString("txnid");
            }else{
                txnid= "Failed";
            }
            if (jsonObject.has("rrn")){
                rrn=jsonObject.getString("rrn");
            }else{
                rrn= "Failed";
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Intent intent=new Intent(Electricity.this, TransactionReciept.class);
        intent.putExtra("status",status);
        intent.putExtra("message",message);
        intent.putExtra("transactionid", txnid);
        intent.putExtra("transactionrrn", rrn);
        intent.putExtra("transaction_type","Electricity Bill Pay");
        intent.putExtra("operator",textview_operator.getText().toString());
        intent.putExtra("number",number);
        intent.putExtra("price",amount);
        intent.putExtra("icon",icon);
        startActivity(intent);
    }

    protected void mShowDialog(final String message)
    {
        final AlertDialog.Builder builder1= new AlertDialog.Builder(Electricity.this);
        builder1.setMessage("Billpay of "+operator_name+ "\nNumber - "+ number + "\nAmount - \u20B9 "+amount + "\nNote - " + message) .setTitle("Billpay Confirmation");

        builder1.setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (DetectConnection.checkInternetConnection(Electricity.this)){
                            mElectricityBillPay(SharePrfeManager.getInstance(Electricity.this).mGetappToken(), SharePrfeManager.getInstance(Electricity.this).mGetUserId(),provider, amount,number,transaction_type,biller_name,mobile_number,selected_due_date,bu);
                            dialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder1.create();
        alert.show();
    }

    protected void mVerifyShowResponse(final String json)
    {
        Log.e("response", json);
        String status="",message="", mystatus ="";
        try
        {
            JSONObject jsonObject=new JSONObject(json);
            if (jsonObject.has("status"))
            {
                status=jsonObject.getString("status");
            }

            if (jsonObject.has("status")){
                message=jsonObject.getString("status");
            }else if (jsonObject.has("message")){
                message=jsonObject.getString("message");
            }

            if (status.equalsIgnoreCase("txn")) {
                JSONObject mydata=jsonObject.getJSONObject("data");
                mystatus = mydata.getString("statuscode");
                if (mystatus.equalsIgnoreCase("txn") && jsonObject.has("data") && !jsonObject.getString("data").equals("")) {

                    JSONObject newdata= mydata.getJSONObject("data");
                    biller_name = newdata.getString("customername");
                    amount = newdata.getString("dueamount");
                    selected_due_date = newdata.getString("duedate");

                    edittext_amount.setText(amount);
                    edittext_biller.setText(biller_name);
                    textview_due_date.setText(selected_due_date);
                }

                Toast.makeText(this, "Bill Details Fetched Successfully", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void mCloseFragment()
    {
        getSupportFragmentManager().popBackStack();
    }
}