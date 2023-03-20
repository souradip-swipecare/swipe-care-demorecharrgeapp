package com.swipecare.payments.recharges;

import android.app.AlertDialog;
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
import android.view.LayoutInflater;
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

/**
 * Created by Basant on 5/23/2017.
 */

public class DTH extends AppCompatActivity  {

    LinearLayout ll_operator;
    TextView textview_operator;


    EditText editext_number;
    public static  EditText edittext_amount;
    Button button_paynow;

    ProgressDialog dialog;

    String username,password,balance,provider="";

    public String amount="",number;
    RelativeLayout rl_message;
    TextView textview_message;


    String operator_name;

    LinearLayout ll_mobile,ll_electricity;

    String icon="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dth_recharge);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ll_mobile=findViewById(R.id.ll_mobile);
        ll_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DTH.this,Mobile_Recharge.class));
                DTH.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        ll_electricity=findViewById(R.id.ll_electricity);
        ll_electricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DTH.this,Electricity.class));
                DTH.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        username=sharedPreferences.getString("username","");
        password=sharedPreferences.getString("password","");
        balance=sharedPreferences.getString("balance","");

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
                bundle.putString("type","2");
                bundle.putString("activity","dth");
                bottomSheetDialogFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.content,bottomSheetDialogFragment).addToBackStack(null).commit();
            }
        });


        button_paynow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (DetectConnection.checkInternetConnection(DTH.this)) {
                    if (editext_number.getText().toString().equals("")) {
                        textview_message.setText("Please enter DTH no");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(DTH.this, R.anim.animation_down);
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
                                Animation a = AnimationUtils.loadAnimation(DTH.this, R.anim.animation_down);
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
                                Animation a = AnimationUtils.loadAnimation(DTH.this, R.anim.animation_down);
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
                            Animation a = AnimationUtils.loadAnimation(DTH.this, R.anim.animation_down);
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

    private  void mDTHRecharge(final  String appToken, final String user_id, final String provider, final String amount, final String number) {
        class getJSONData extends AsyncTask<String, String, String> {


            HttpURLConnection urlConnection;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog=new ProgressDialog(DTH.this);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
            }
            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    URL url = new URL(SharePrfeManager.getInstance(DTH.this).mGetBaseUrl()+"api/android/recharge/pay?apptoken="+appToken+"&user_id="+user_id+"&provider_id="+provider+"&amount="+amount+"&number="+number);
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

                //Do something with the JSON string
                dialog.dismiss();

              //  Log.e("data",result);
                Showdata(result);
            }

        }

        getJSONData getJSONData=new getJSONData();
        getJSONData.execute();
    }


    //    to get data from cardadapter
    public void GetData(String provider_id,String name)
    {
        operator_name=name;
        textview_operator.setText(operator_name);
//        BottomSheet3DialogFragment.dialogFragment.dismiss();
        provider=provider_id;

    }


    protected void Showdata(final String json)
    {
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

        Intent intent=new Intent(DTH.this, TransactionReciept.class);
        intent.putExtra("status",status);
        intent.putExtra("message",message);
        intent.putExtra("transactionid", txnid);
        intent.putExtra("transactionrrn", rrn);
        intent.putExtra("transaction_type","Dth Recharge");
        intent.putExtra("operator",textview_operator.getText().toString());
        intent.putExtra("number",number);
        intent.putExtra("price",amount);
        intent.putExtra("icon",icon);
        startActivity(intent);
    }

    protected void mShowDialog(final String message)
    {
        final AlertDialog.Builder builder1= new AlertDialog.Builder(DTH.this);
        builder1.setMessage("Recharge of "+operator_name + "\nAmount - \u20B9 "+amount + "\nNote - " + message) .setTitle("Recharge Confirmation");

        builder1.setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (DetectConnection.checkInternetConnection(DTH.this)){
                            mDTHRecharge(SharePrfeManager.getInstance(DTH.this).mGetappToken(), SharePrfeManager.getInstance(DTH.this).mGetUserId(),provider,amount,number);
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

    protected void mShowResponse(final String status,final String message)
    {
        final AlertDialog alertDialog;
        LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v2 = inflater2.inflate(R.layout.custome_layout_fund_transfer, null);

        Button button_ok = v2.findViewById(R.id.button_ok);
        ImageView imageview_status_image = v2.findViewById(R.id.imageview_status_image);
        TextView textview_message = v2.findViewById(R.id.textview_message);

        if (status.equalsIgnoreCase("err"))
        {
            imageview_status_image.setImageDrawable(getResources().getDrawable(R.drawable.error_icon));
            textview_message.setText(message);
            textview_message.setTextColor(getResources().getColor(R.color.orange));
        }
        else if (status.equalsIgnoreCase("txn"))
        {
            imageview_status_image.setImageDrawable(getResources().getDrawable(R.drawable.success));
            textview_message.setText(message);
            textview_message.setTextColor(getResources().getColor(R.color.green));
        }
        else
        {
            imageview_status_image.setImageDrawable(getResources().getDrawable(R.drawable.error_icon));
            textview_message.setText(message);
            textview_message.setTextColor(getResources().getColor(R.color.orange));
        }


        final AlertDialog.Builder builder2 = new AlertDialog.Builder(DTH.this);
        builder2.setCancelable(false);

        builder2.setView(v2);

        alertDialog = builder2.create();
        button_ok.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        if (status.equalsIgnoreCase("txn"))
                        {
                            finish();
                        }

                    }
                });

        alertDialog.show();
    }

    public void mCloseFragment()
    {
        getSupportFragmentManager().popBackStack();
    }

}