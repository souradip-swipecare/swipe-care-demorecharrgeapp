package com.swipecare.payments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class WalletRequest extends AppCompatActivity {

    RelativeLayout ll_payment_bank;
    TextView textview_payment_bank;

    RelativeLayout ll_payment_type;
    TextView textview_payment_type;

    EditText edittext_amount,edittext_referenceno;

    RelativeLayout ll_payment_date;
    TextView textview_payment_date;

    Button button_submit;
    ProgressDialog dialog;

    PopupMenu popupMenu_for_payment_bank;
    PopupMenu popupMenu_for_payment_mode;

    int mYear,mMonth,mDay;
    String selected_bank_id="",selected_payment_mode_id="",selected_date="",icon="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_request);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ll_payment_bank=findViewById(R.id.ll_payment_bank);
        textview_payment_bank=findViewById(R.id.textview_payment_bank);
        ll_payment_type=findViewById(R.id.ll_payment_type);
        textview_payment_type=findViewById(R.id.textview_payment_type);
        edittext_amount=findViewById(R.id.edittext_amount);
        edittext_referenceno=findViewById(R.id.edittext_referenceno);
        ll_payment_date=findViewById(R.id.ll_payment_date);
        textview_payment_date=findViewById(R.id.textview_payment_date);
        button_submit=findViewById(R.id.button_submit);

        ll_payment_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(WalletRequest.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                         selected_date=  dayOfMonth+ "-" + (monthOfYear + 1) + "-" +year;
                        textview_payment_date.setText(selected_date);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });
        popupMenu_for_payment_bank=new PopupMenu(WalletRequest.this,ll_payment_bank);
        ll_payment_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu_for_payment_bank.show();
            }
        });

        popupMenu_for_payment_bank.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                textview_payment_bank.setText(item.getTitle().toString());
                selected_bank_id=item.getGroupId()+"";
                return true;
            }
        });

        popupMenu_for_payment_mode=new PopupMenu(WalletRequest.this,ll_payment_type);
        ll_payment_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu_for_payment_mode.show();
            }
        });
        popupMenu_for_payment_mode.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                textview_payment_type.setText(item.getTitle().toString());
                selected_payment_mode_id=item.getGroupId()+"";
                return true;
            }
        });

        if (DetectConnection.checkInternetConnection(WalletRequest.this)) {
            mGetBankList();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(WalletRequest.this)) {
                    if (selected_bank_id.equals("")) {
                        Toast.makeText(WalletRequest.this, "Please select bank", Toast.LENGTH_SHORT).show();
                    } else if (selected_payment_mode_id.equals("")) {
                        Toast.makeText(WalletRequest.this, "Please select payment mode", Toast.LENGTH_SHORT).show();
                    } else if (edittext_amount.getText().toString().equals("")) {
                        Toast.makeText(WalletRequest.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                    } else if (selected_date.equals("")) {
                        Toast.makeText(WalletRequest.this, "Please select payment date", Toast.LENGTH_SHORT).show();
                    } else {
                        String amount=edittext_amount.getText().toString();
                        String reference_no=edittext_referenceno.getText().toString();
                        mSubmitPaymentRequest(amount,reference_no);
                    }
                } else {
                    Toast.makeText(WalletRequest.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private  void  mGetBankList() {
        class getJSONData extends AsyncTask<String, String, String> {
            HttpURLConnection urlConnection;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog=new ProgressDialog(WalletRequest.this);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
            }
            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    Log.e("url", SharePrfeManager.getInstance(WalletRequest.this).mGetBaseUrl()+"api/android/fundrequest?apptoken="+SharePrfeManager.getInstance(WalletRequest.this).mGetappToken()+"&type=getfundbank&user_id="+SharePrfeManager.getInstance(WalletRequest.this).mGetUserId());
                    URL url = new URL(SharePrfeManager.getInstance(WalletRequest.this).mGetBaseUrl()+"api/android/fundrequest?apptoken="+SharePrfeManager.getInstance(WalletRequest.this).mGetappToken()+"&type=getfundbank&user_id="+SharePrfeManager.getInstance(WalletRequest.this).mGetUserId());
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
                String myresult="";
                myresult=result;
                if (!myresult.equals("")) {
                    String status="";
                    String message="";
                    try
                    {
                        JSONObject jsonObject=new JSONObject(myresult);
                        if (jsonObject.has("statuscode")){
                            status=jsonObject.getString("statuscode");
                        }

                        if (jsonObject.has("message")) {
                            message=jsonObject.getString("message");
                        }

                        if (status.equalsIgnoreCase("txn")) {
                            JSONObject data=jsonObject.getJSONObject("data");
                            JSONArray jsonArray=data.getJSONArray("banks");
                            if (jsonArray!=null) {
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject banks=jsonArray.getJSONObject(i);
                                    popupMenu_for_payment_bank.getMenu().add(banks.getInt("id"),0,0,banks.getString("name")+"\nA/c : "+banks.getString("account")+"\nIFSC : "+banks.getString("ifsc")+"\nBranch: "+banks.getString("branch"));
                                }
                            }

                            JSONArray jsonArray1=data.getJSONArray("paymodes");
                            if (jsonArray1!=null) {
                                for (int i=0; i<jsonArray1.length(); i++) {
                                    JSONObject paymodes=jsonArray1.getJSONObject(i);
                                    popupMenu_for_payment_mode.getMenu().add(paymodes.getInt("id"),0,0,paymodes.getString("name"));
                                }
                            }
                        } else {
                            Toast.makeText(WalletRequest.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(WalletRequest.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }
        getJSONData getJSONData=new getJSONData();
        getJSONData.execute();
    }

    private  void  mSubmitPaymentRequest(final  String amount,final String referenece_no) {
        class getJSONData extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(WalletRequest.this);
            dialog.setMessage("Please wait...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(SharePrfeManager.getInstance(WalletRequest.this).mGetBaseUrl()+"api/android/fundrequest?apptoken="+SharePrfeManager.getInstance(WalletRequest.this).mGetappToken()+"&type=request&user_id="+SharePrfeManager.getInstance(WalletRequest.this).mGetUserId()+"&fundbank_id="+selected_bank_id+"&paymode="+selected_payment_mode_id+"&amount="+amount+"&ref_no="+referenece_no+"&paydate="+selected_date);
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
            String myresult="";
            myresult=result;

            if (!myresult.equals("")) {
                String status="";
                String message="";
                String txnid="";

                try {
                    JSONObject jsonObject=new JSONObject(myresult);
                    if (jsonObject.has("statuscode")) {
                        status=jsonObject.getString("statuscode");
                    }else{
                        status= "ERR";
                    }

                    if (jsonObject.has("message")) {
                        message=jsonObject.getString("message");
                    }else{
                        message= "Something went wrong, try again";
                    }

                    if (status.equalsIgnoreCase("txn")) {
                        txnid = jsonObject.getString("txnid");
                        Intent intent=new Intent(WalletRequest.this, TransactionReciept.class);
                        intent.putExtra("status",status);
                        intent.putExtra("message",message);
                        intent.putExtra("transactionid", txnid);
                        intent.putExtra("transactionrrn", txnid);
                        intent.putExtra("transaction_type","Load Wallet Request");
                        intent.putExtra("operator", textview_payment_bank.getText().toString());
                        intent.putExtra("number", referenece_no);
                        intent.putExtra("price",amount);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(WalletRequest.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(WalletRequest.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
