package com.swipecare.payments.recharges;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class Mobile_Recharge extends AppCompatActivity  {

    LinearLayout ll_operator;
    TextView textview_operator;


    EditText editext_number;
    public static  EditText edittext_amount;
    Button button_paynow;

    ProgressDialog dialog;

    String myJSON;

    String username,password,balance,provider="";


    public String amount="",number;
    RelativeLayout rl_message;
    TextView textview_message;


    ImageView imageview_contact;

    String operator_name,provider_image;

    LinearLayout ll_dth,ll_electricity;

    String icon="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    // Inflate the layout for this fragment
        setContentView(R.layout.mobile_recharges);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ll_dth=findViewById(R.id.ll_dth);
        ll_dth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Mobile_Recharge.this,DTH.class));
                Mobile_Recharge.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        ll_electricity=findViewById(R.id.ll_electricity);
        ll_electricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Mobile_Recharge.this,Electricity.class));
                Mobile_Recharge.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        username=sharedPreferences.getString("username","");
        password=sharedPreferences.getString("password","");
        balance=sharedPreferences.getString("balance","");


        imageview_contact=(ImageView)findViewById(R.id.imageview_contact);
        imageview_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 10);
            }
        });


        button_paynow = findViewById(R.id.button_paynow);
        editext_number = findViewById(R.id.edittext_enter_number);
        edittext_amount = (EditText) findViewById(R.id.edittext_amount);

        rl_message= findViewById(R.id.rl_message);
        textview_message= findViewById(R.id.textview_message);


        ll_operator= findViewById(R.id.ll_operator);
        textview_operator= findViewById(R.id.textview_operator);
        ll_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OperatorsFragment bottomSheetDialogFragment = new OperatorsFragment();
                Bundle bundle=new Bundle();
                bundle.putString("type","1");
                bundle.putString("activity","mobile");
                bottomSheetDialogFragment.setArguments(bundle);

                FragmentManager fragmentManager=getSupportFragmentManager();
                getSupportFragmentManager().beginTransaction().add(R.id.content,bottomSheetDialogFragment).addToBackStack(null).commit();

            }
        });


        button_paynow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (DetectConnection.checkInternetConnection(Mobile_Recharge.this)) {
                    if (editext_number.getText().toString().equals("")) {
                        textview_message.setText("Please enter mobileno");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Mobile_Recharge.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    } else if (editext_number.getText().toString().length() < 10) {
                        textview_message.setText("Please enter a valid mobileno");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Mobile_Recharge.this, R.anim.animation_down);
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
                                Animation a = AnimationUtils.loadAnimation(Mobile_Recharge.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    } else if (edittext_amount.getText().toString().equals("")) {
                        textview_message.setText("Please enter amount");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Mobile_Recharge.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                    else {
//
                         number = editext_number.getText().toString();
                         amount = edittext_amount.getText().toString();
                         mShowDialog("Please confirm operator, number and amount, wrong recharge will not reverse at any circumstances.");

                    }
                }
                else
                {
                    textview_message.setText("No Internet Connection");
                    rl_message.setVisibility(View.VISIBLE);
                    rl_message.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation a = AnimationUtils.loadAnimation(Mobile_Recharge.this, R.anim.animation_down);
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

    private  void  mRechargeMobile(final  String appToken,final String user_id,final String provider,final String amount,final String number) {
        class getJSONData extends AsyncTask<String, String, String> {

            HttpURLConnection urlConnection;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog=new ProgressDialog(Mobile_Recharge.this);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
            }
            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    Log.e("url", SharePrfeManager.getInstance(Mobile_Recharge.this).mGetBaseUrl()+"api/android/recharge/pay?apptoken="+appToken+"&user_id="+user_id+"&provider_id="+provider+"&amount="+amount+"&number="+number);
                    URL url = new URL(SharePrfeManager.getInstance(Mobile_Recharge.this).mGetBaseUrl()+"api/android/recharge/pay?apptoken="+appToken+"&user_id="+user_id+"&provider_id="+provider+"&amount="+amount+"&number="+number);
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

                Log.e("data",result);
                Showdata(result);
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

    }

    protected void Showdata(final String json)
    {
        Log.e("response", json);
        String status="";
        String message="";
        String txnid="";
        String rrn="";
        try {
            JSONObject jsonObject = new JSONObject(json);
            status=jsonObject.getString("status");
            if (status.equalsIgnoreCase("ERR")){
                message=jsonObject.getString("message");
            }else {
                message = status;//jsonObject.getString("description");
            }
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
        catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent=new Intent(Mobile_Recharge.this, TransactionReciept.class);
        intent.putExtra("status",status);
        intent.putExtra("message",message);
        intent.putExtra("transactionid", txnid);
        intent.putExtra("transactionrrn", rrn);
        intent.putExtra("transaction_type","Mobile Recharge");
        intent.putExtra("operator",textview_operator.getText().toString());
        intent.putExtra("number",number);
        intent.putExtra("price",amount);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (10) :
                if (resultCode == Activity.RESULT_OK) {
                    Cursor cursor = null;
                    try {
                        String phoneNo = null ;
                        String name = null;
                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        phoneNo = cursor.getString(phoneIndex);
                        phoneNo=phoneNo.replaceAll(" ","");

                        if (phoneNo.startsWith("+91"))
                        {
                            editext_number.setText(phoneNo.substring(3,13));
                        }

                        else {
                            editext_number.setText(phoneNo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case (12):
                if (resultCode == Activity.RESULT_OK) {
                    Bundle res = data.getExtras();
                    String result = res.getString("qr_code");
                    editext_number.setText(result);

                }else if(resultCode==Activity.RESULT_CANCELED){
                    Toast.makeText(getApplicationContext(),"Unable to read QR Code.Please Try Again.",Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    protected void mShowDialog(final String message)
    {
        final AlertDialog.Builder builder1= new AlertDialog.Builder(Mobile_Recharge.this);
        builder1.setMessage("Recharge of "+operator_name + "\nAmount - \u20B9 "+amount + "\nNote - " + message) .setTitle("Recharge Confirmation");

        builder1.setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (DetectConnection.checkInternetConnection(Mobile_Recharge.this)){
                            mRechargeMobile(SharePrfeManager.getInstance(Mobile_Recharge.this).mGetappToken(), SharePrfeManager.getInstance(Mobile_Recharge.this).mGetUserId(),provider,amount,number);
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

    public void mCloseFragment()
    {
        getSupportFragmentManager().popBackStack();
    }


}