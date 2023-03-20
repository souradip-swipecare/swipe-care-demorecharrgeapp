package com.swipecare.payments.PanCards;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.swipecare.payments.DetectConnection;
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


public class PanCard extends AppCompatActivity {


    LinearLayout ll_pancard_detail;
    EditText edittext_vle_id;
    RelativeLayout rl_message;
    TextView textview_message;

    Button button_purcharge_now;
    String selected_no_of_token="";

    ProgressDialog dialog;

    EditText edittext_no_of_tokens, editText_amount;

    RelativeLayout rl_create_utiid;
    TextView textview_uttid_message;
    Button button_create;

    int token_amount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_card);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ll_pancard_detail=findViewById(R.id.ll_pancard_detail);
        rl_create_utiid=findViewById(R.id.rl_create_utiid);

        edittext_vle_id=findViewById(R.id.edittext_vle_id);

        if (!SharePrfeManager.getInstance(PanCard.this).mGetTokenAmount().equals("")) {
            token_amount = Integer.parseInt(SharePrfeManager.getInstance(PanCard.this).mGetTokenAmount());
        }

        if (SharePrfeManager.getInstance(PanCard.this).mGetUtiid().equalsIgnoreCase("no"))
        {
            ll_pancard_detail.setVisibility(View.GONE);
            rl_create_utiid.setVisibility(View.VISIBLE);
        }
        else
        {
            ll_pancard_detail.setVisibility(View.VISIBLE);
            rl_create_utiid.setVisibility(View.GONE);
        }

        edittext_vle_id.setText(SharePrfeManager.getInstance(PanCard.this).mGetUtiid());

        edittext_no_of_tokens=findViewById(R.id.edittext_no_of_tokens);
        edittext_no_of_tokens.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(""))
                {
                    int total_amount=(Integer.parseInt(s.toString())*token_amount);
                    editText_amount.setText("" +total_amount);
                }
                else {
                    editText_amount.setText("0");
                }
            }
        });

        editText_amount=findViewById(R.id.textview_amount);

        textview_uttid_message=findViewById(R.id.textview_uttid_message);
        button_create=findViewById(R.id.button_create);

        rl_message=findViewById(R.id.rl_message);
        textview_message=findViewById(R.id.textview_message);
        button_purcharge_now=findViewById(R.id.button_purcharge_now);

        button_purcharge_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(PanCard.this)) {
                    if (edittext_no_of_tokens.getText().toString().equals("")) {
                        textview_message.setText("Please select no of token");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(PanCard.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    } else {
                        selected_no_of_token = edittext_no_of_tokens.getText().toString();
                        mPurchargeToken(SharePrfeManager.getInstance(PanCard.this).mGetappToken(), SharePrfeManager.getInstance(PanCard.this).mGetUserId(), SharePrfeManager.getInstance(PanCard.this).mGetUtiid(), selected_no_of_token);
                    }
                } else {
                    textview_message.setText("No internet connection");
                    rl_message.setVisibility(View.VISIBLE);
                    rl_message.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation a = AnimationUtils.loadAnimation(PanCard.this, R.anim.animation_down);
                            rl_message.setAnimation(a);
                            rl_message.clearAnimation();
                            rl_message.setVisibility(View.GONE);
                        }
                    }, 3000);
                }
            }
        });


        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(PanCard.this)) {
                    mCreateUtiid(SharePrfeManager.getInstance(PanCard.this).mGetappToken(),SharePrfeManager.getInstance(PanCard.this).mGetUserId(),"utiid");
                }
                else
                {
                    textview_message.setText("No internet connection");
                    rl_message.setVisibility(View.VISIBLE);
                    rl_message.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation a = AnimationUtils.loadAnimation(PanCard.this, R.anim.animation_down);
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

    private  void  mPurchargeToken(final  String appToken,final String user_id,final String utiid,final String no_of_token) {
        class getJSONData extends AsyncTask<String, String, String> {
            HttpURLConnection urlConnection;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog=new ProgressDialog(PanCard.this);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
            }
            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    URL url = new URL(SharePrfeManager.getInstance(PanCard.this).mGetBaseUrl()+"api/android/pancard/transaction?apptoken="+appToken+"&user_id="+user_id+"&type=token&vleid="+utiid+"&tokens="+no_of_token);
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
                String status="";
                String message="";
                String txnid="";
                String rrn="";
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    status=jsonObject.getString("statuscode");
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

                Intent intent=new Intent(PanCard.this, TransactionReciept.class);
                intent.putExtra("status",status);
                intent.putExtra("message",message);
                intent.putExtra("transactionid", txnid);
                intent.putExtra("transactionrrn", rrn);
                intent.putExtra("transaction_type","Pancard Token");
                intent.putExtra("operator","Utipancard");
                intent.putExtra("number",utiid);
                intent.putExtra("price",""+Integer.parseInt(no_of_token)*token_amount);
                startActivity(intent);
            }
        }

        getJSONData getJSONData=new getJSONData();
        getJSONData.execute();
    }

    protected void mShowResponse(final String status,final String message) {
        final AlertDialog.Builder builder1= new AlertDialog.Builder(PanCard.this);
        builder1.setMessage(message);

        builder1.setCancelable(false)
                .setTitle("Success")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder1.create();
        alert.show();
    }

    private  void  mCreateUtiid(final  String appToken,final String user_id,final String type) {
        class getJSONData extends AsyncTask<String, String, String> {

            HttpURLConnection urlConnection;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(PanCard.this);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    Log.e("url", SharePrfeManager.getInstance(PanCard.this).mGetBaseUrl() + "api/android/pancard/transaction?apptoken=" + appToken + "&user_id=" + user_id + "&type=" + type);
                    URL url = new URL(SharePrfeManager.getInstance(PanCard.this).mGetBaseUrl() + "api/android/pancard/transaction?apptoken=" + appToken + "&user_id=" + user_id + "&type=" + type);
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
                Log.e("data", result);
                String status = "";
                String message = "";

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("statuscode")) {
                        status = jsonObject.getString("statuscode");
                    }else{
                        status = "ERR";
                    }

                    if (jsonObject.has("message")) {
                        message = jsonObject.getString("message");
                    }else{
                        message = "Something went wrong";
                    }

                    if (status.equalsIgnoreCase("txn")) {
                        SharePrfeManager.getInstance(PanCard.this).mSetUtiid("pending");
                        mShowResponse(status, message);
                    } else {
                        Toast.makeText(PanCard.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        getJSONData getJSONData = new getJSONData();
        getJSONData.execute();
    }
}
