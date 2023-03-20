package com.swipecare.payments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class AEPS_Wallet_Request extends AppCompatActivity {
    RelativeLayout rl_type;
    TextView textview_type;
    LinearLayout ll_bank_detail;
    EditText edittext_bank_name,edittext_account_number,edittext_ifsc,edittext_amount;
    Button button_submit;
    String selected_payment_type="";

    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps__wallet__request);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rl_type=findViewById(R.id.rl_type);
        textview_type=findViewById(R.id.textview_type);
        rl_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(AEPS_Wallet_Request.this,rl_type);
                popupMenu.getMenu().add("Wallet");
                popupMenu.getMenu().add("Bank");

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        selected_payment_type=item.getTitle().toString().toLowerCase();

                        textview_type.setText(selected_payment_type);
                        return true;
                    }
                });
            }
        });

        ll_bank_detail=findViewById(R.id.ll_bank_detail);


        edittext_bank_name=findViewById(R.id.edittext_bank_name);
        edittext_bank_name.setText(SharePrfeManager.getInstance(AEPS_Wallet_Request.this).mGetBank());

        edittext_account_number=findViewById(R.id.edittext_account_number);
        edittext_account_number.setText(SharePrfeManager.getInstance(AEPS_Wallet_Request.this).mGetAccount());

        edittext_ifsc=findViewById(R.id.edittext_ifsc);
        edittext_ifsc.setText(SharePrfeManager.getInstance(AEPS_Wallet_Request.this).mGetIFSC());

        edittext_amount=findViewById(R.id.edittext_amount);
        button_submit=findViewById(R.id.button_submit);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(AEPS_Wallet_Request.this)){
                    if (selected_payment_type.equalsIgnoreCase("")) {
                        Toast.makeText(AEPS_Wallet_Request.this, "Please select payment type", Toast.LENGTH_SHORT).show();
                    } else if (selected_payment_type.equalsIgnoreCase("wallet")&&edittext_amount.getText().toString().equals("")) {
                        Toast.makeText(AEPS_Wallet_Request.this, "PLease enter request amount", Toast.LENGTH_SHORT).show();
                    }else if (selected_payment_type.equalsIgnoreCase("bank")&&edittext_bank_name.getText().toString().equals("")) {
                        Toast.makeText(AEPS_Wallet_Request.this, "Please enter bank name were amount deposited", Toast.LENGTH_SHORT).show();
                    }else if (selected_payment_type.equalsIgnoreCase("bank") && edittext_account_number.getText().toString().equals("")) {
                        Toast.makeText(AEPS_Wallet_Request.this, "Please enter account number in which amount deposited", Toast.LENGTH_SHORT).show();
                    }else if (selected_payment_type.equalsIgnoreCase("bank")&&edittext_ifsc.getText().toString().equals("")) {
                        Toast.makeText(AEPS_Wallet_Request.this, "Please enter bank IFSC", Toast.LENGTH_SHORT).show();
                    }else if (selected_payment_type.equalsIgnoreCase("bank")&&edittext_amount.getText().toString().equals("")) {
                        Toast.makeText(AEPS_Wallet_Request.this, "Please enter request amount", Toast.LENGTH_SHORT).show();
                    }else {
                        String bank_name=edittext_bank_name.getText().toString().replaceAll(" ","%20");
                        String account=edittext_account_number.getText().toString();
                        String ifsc=edittext_ifsc.getText().toString();
                        String amount=edittext_amount.getText().toString();
                        mSubmitRequest(SharePrfeManager.getInstance(AEPS_Wallet_Request.this).mGetappToken(),SharePrfeManager.getInstance(AEPS_Wallet_Request.this).mGetUserId(),selected_payment_type,amount,account,bank_name,ifsc);
                    }
                }else{
                    Toast.makeText(AEPS_Wallet_Request.this, "NO internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private  void  mSubmitRequest(final  String apptoken,final String user_id,final String payment_type,final String amount,final String account_number,final String bank,final String ifsc) {
        class getJSONData extends AsyncTask<String, String, String> {
            HttpURLConnection urlConnection;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog=new ProgressDialog(AEPS_Wallet_Request.this);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);

            }
            @Override
            protected String doInBackground(String... args) {

                BufferedReader reader;
                StringBuffer buffer;
                String res = null;

                try {
                    Log.e("url", SharePrfeManager.getInstance(AEPS_Wallet_Request.this).mGetBaseUrl()+"api/android/fundrequest?apptoken="+apptoken+"&user_id="+user_id+
                            "&type="+payment_type+"&amount="+amount+"&account="+account_number+"&bank="+bank+"&ifsc="+ifsc);
                    URL url = new URL(SharePrfeManager.getInstance(AEPS_Wallet_Request.this).mGetBaseUrl()+"api/android/fundrequest?apptoken="+apptoken+"&user_id="+user_id+
                            "&type="+payment_type+"&amount="+amount+"&account="+account_number+"&bank="+bank+"&ifsc="+ifsc);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(40000);
                    con.setConnectTimeout(40000);
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Accept","application/json");
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    con.setDoInput(true);
                    con.setDoOutput(true);

                    int status = con.getResponseCode();
                    InputStream inputStream;
                    if (status == HttpURLConnection.HTTP_OK) {
                        inputStream = con.getInputStream();
                    } else {
                        inputStream = con.getErrorStream();
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    buffer = new StringBuffer();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    res = buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return res;
            }

            @Override
            protected void onPostExecute(String result) {
                dialog.dismiss();
                Log.e("submit response",result);

                if (!result.equals("")) {
                    String statuscode="";
                    String message="";
                    String txnid="";
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        if (jsonObject.has("statuscode")) {
                            statuscode = jsonObject.getString("statuscode");
                        }else{
                            statuscode = "ERR";
                        }

                        if (jsonObject.has("message")) {
                            message = jsonObject.getString("message");
                        }else{
                            message = "Something went wrong";
                        }

                        if (!statuscode.equals("")) {
                            if (statuscode.equalsIgnoreCase("txn")){
                                txnid = jsonObject.getString("txnid");
                                Intent intent=new Intent(AEPS_Wallet_Request.this, TransactionReciept.class);
                                intent.putExtra("status",statuscode);
                                intent.putExtra("message",message);
                                intent.putExtra("transactionid", txnid);
                                intent.putExtra("transactionrrn", txnid);
                                intent.putExtra("transaction_type","Aeps Fund Request");
                                intent.putExtra("operator", payment_type);
                                intent.putExtra("number", account_number);
                                intent.putExtra("price",amount);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(AEPS_Wallet_Request.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(AEPS_Wallet_Request.this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(AEPS_Wallet_Request.this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                }
            }
        }

        getJSONData getJSONData=new getJSONData();
        getJSONData.execute();
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