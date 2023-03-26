package com.swipecare.payments.TransactionReports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.swipecare.payments.DetectConnection;
import com.swipecare.payments.R;
import com.swipecare.payments.SharePrfeManager;
import com.swipecare.payments.recharges.DTH;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TransactionReports extends AppCompatActivity {


    SwipeRefreshLayout swiprefresh_aeps_report;
    RecyclerView recyclerview_eaps_transaction_reports;
    TextView textview_message;
    List<TransactionReportsItems> transactionReportsItems;
    TransactionReportsCardAdapter transactionReportsCardAdapter;
    ProgressDialog dialog;
    String type="";
    public static boolean last_array_empty=false;
    public static int start_page=0;
    boolean swiped_refresh=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_reports);

        start_page=1;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        type=getIntent().getStringExtra("type");

        switch (type){
            case "aepsstatement":
                getSupportActionBar().setTitle("Aeps Transaction");
                break;

            case "billpaystatement":
                getSupportActionBar().setTitle("Billpay Transaction");
                break;

            case "dmtstatement":
                getSupportActionBar().setTitle("Money Transfer Transaction");
                break;

            case "matmstatement":
                getSupportActionBar().setTitle("Micro Atm Transaction");
                break;

            case "rechargestatement":
                getSupportActionBar().setTitle("Recharge Transaction");
                break;
        }

        swiprefresh_aeps_report=findViewById(R.id.swiprefresh_aeps_report);
        recyclerview_eaps_transaction_reports=findViewById(R.id.recyclerview_eaps_transaction_reports);
        recyclerview_eaps_transaction_reports.setLayoutManager(new LinearLayoutManager(TransactionReports.this));
        transactionReportsItems=new ArrayList<>();
        transactionReportsCardAdapter=new TransactionReportsCardAdapter(TransactionReports.this,transactionReportsItems);
        recyclerview_eaps_transaction_reports.setAdapter(transactionReportsCardAdapter);
        textview_message=findViewById(R.id.textview_message);


        swiprefresh_aeps_report.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swiprefresh_aeps_report.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (DetectConnection.checkInternetConnection(TransactionReports.this))
                {
                    start_page=1;
                    swiped_refresh=true;

                    textview_message.setVisibility(View.GONE);
                    recyclerview_eaps_transaction_reports.setVisibility(View.VISIBLE);
                    mGetTransactionReports(SharePrfeManager.getInstance(TransactionReports.this).mGetappToken(),SharePrfeManager.getInstance(TransactionReports.this).mGetUserId(),type);
                }
                else
                {
                    swiprefresh_aeps_report.setRefreshing(false);
                    textview_message.setText("No internet connection");
                    textview_message.setVisibility(View.VISIBLE);
                    recyclerview_eaps_transaction_reports.setVisibility(View.GONE);
                }
            }
        });


        if (DetectConnection.checkInternetConnection(TransactionReports.this))
        {
            textview_message.setVisibility(View.GONE);
            recyclerview_eaps_transaction_reports.setVisibility(View.VISIBLE);
            mGetTransactionReports(SharePrfeManager.getInstance(TransactionReports.this).mGetappToken(),SharePrfeManager.getInstance(TransactionReports.this).mGetUserId(),type);
        }
        else
        {
            textview_message.setVisibility(View.VISIBLE);
            textview_message.setText("No internet connection");
            recyclerview_eaps_transaction_reports.setVisibility(View.GONE);
        }


    }

    private void complainactivity() {

    }


    private  void  mGetTransactionReports(final  String apptoken,final String user_id,final String type) {
        class getJSONData extends AsyncTask<String, String, String> {
            HttpURLConnection urlConnection;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog=new ProgressDialog(TransactionReports.this);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
                swiprefresh_aeps_report.setRefreshing(true);
            }
            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();
                try {
                    Log.e("url", SharePrfeManager.getInstance(TransactionReports.this).mGetBaseUrl()+"api/android/transaction?apptoken="+apptoken+"&user_id="+user_id+"&type="+type+"&start="+start_page);
                    URL url = new URL(SharePrfeManager.getInstance(TransactionReports.this).mGetBaseUrl()+"api/android/transaction?apptoken="+apptoken+"&user_id="+user_id+"&type="+type+"&start="+start_page);
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

                swiprefresh_aeps_report.setRefreshing(false);
                dialog.dismiss();
//                Log.e("login response",result);
                if (!result.equals(""))
                {
                    try
                    {
                        JSONObject jsonObject=new JSONObject(result);
                        if (jsonObject.has("statuscode"))
                        {
                            if (jsonObject.getString("statuscode").equalsIgnoreCase("txn"))
                            {
                                if (swiped_refresh) {
                                    transactionReportsItems.clear();
                                }

                                JSONArray jsonArray=jsonObject.getJSONArray("data");
                                for (int i=0; i<jsonArray.length(); i++)
                                {
                                    JSONObject data=jsonArray.getJSONObject(i);
                                    TransactionReportsItems items=new TransactionReportsItems();
                                    items.setId(data.getString("id"));
                                    items.setMobile(data.getString("mobile"));
                                    items.setNumber(data.getString("number"));
                                    items.setProfit(data.getString("txnid"));
                                    items.setAmount(data.getString("amount"));
                                    items.setRefno(data.getString("refno"));
                                    items.setStatus(data.getString("status"));
                                    items.setProvider(data.getString("providername"));
                                    items.setCreated_at(data.getString("created_at"));

                                    transactionReportsItems.add(items);
                                    transactionReportsCardAdapter.notifyDataSetChanged();
                                }

                                if (jsonArray.length()!=0)
                                {
                                    last_array_empty=false;
                                }
                                else
                                {
                                    last_array_empty=true;
                                }
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    textview_message.setText("Something went wrong");
                    textview_message.setVisibility(View.VISIBLE);
                    recyclerview_eaps_transaction_reports.setVisibility(View.GONE);
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

    public void mCallNextList()
    {
        start_page+=1;

        if (DetectConnection.checkInternetConnection(TransactionReports.this))
        {
            mGetTransactionReports(SharePrfeManager.getInstance(TransactionReports.this).mGetappToken(),SharePrfeManager.getInstance(TransactionReports.this).mGetUserId(),type);

        }
    }



}
