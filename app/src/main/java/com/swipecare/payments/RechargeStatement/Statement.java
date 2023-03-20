package com.swipecare.payments.RechargeStatement;

import android.os.AsyncTask;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.swipecare.payments.DetectConnection;
import com.swipecare.payments.R;
import com.swipecare.payments.SharePrfeManager;

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

public class Statement extends AppCompatActivity {


    SwipeRefreshLayout swiprefresh_recharge_statement;
    RecyclerView recyclerview_statement;
    List<Items> items;
    CardAdapter cardAdapter;
    TextView textview_message, complain;

    String activity="",type="";


    public static boolean last_array_empty=false;
    public static int start_page=0;
    boolean swiped_refresh=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        start_page=1;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swiprefresh_recharge_statement=findViewById(R.id.swiprefresh_recharge_statement);
         complain = findViewById(R.id.complain);
        recyclerview_statement=findViewById(R.id.recyclerview_statement);
        recyclerview_statement.setLayoutManager(new LinearLayoutManager(Statement.this));
        items=new ArrayList<>();
        cardAdapter=new CardAdapter(Statement.this,items);
        recyclerview_statement.setAdapter(cardAdapter);

        textview_message=findViewById(R.id.textview_message);

        activity=getIntent().getStringExtra("activity");

        if (activity.equalsIgnoreCase("recharge"))
        {
            type="rechargestatement";
            getSupportActionBar().setTitle("Recharge Statement");
        }
        else
        {
            type="billpaystatement";
            getSupportActionBar().setTitle("Bill Payment Statement");
        }

        if (DetectConnection.checkInternetConnection(Statement.this))
        {
            mGetStatementData(SharePrfeManager.getInstance(Statement.this).mGetappToken(),SharePrfeManager.getInstance(Statement.this).mGetUserId(),type);
        }
        else
        {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        swiprefresh_recharge_statement.setColorSchemeResources(R.color.colorPrimary);
        swiprefresh_recharge_statement.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (DetectConnection.checkInternetConnection(Statement.this))
                {
                    start_page=1;
                    swiped_refresh=true;

                    mGetStatementData(SharePrfeManager.getInstance(Statement.this).mGetappToken(),SharePrfeManager.getInstance(Statement.this).mGetUserId(),type);
                    textview_message.setVisibility(View.GONE);
                    recyclerview_statement.setVisibility(View.VISIBLE);
                }
                else
                {
                    textview_message.setText("No internet connection");
                    textview_message.setVisibility(View.VISIBLE);
                    recyclerview_statement.setVisibility(View.GONE);
                    swiprefresh_recharge_statement.setRefreshing(false);
                }
            }
        });
    }

    private  void mGetStatementData(final  String appToken, final String user_id, final String type) {
        class getJSONData extends AsyncTask<String, String, String> {


            HttpURLConnection urlConnection;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (swiprefresh_recharge_statement.isRefreshing())
                {

                }
                else {
                    swiprefresh_recharge_statement.setRefreshing(true);
                }

            }
            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    URL url = new URL(SharePrfeManager.getInstance(Statement.this).mGetBaseUrl()+"api/android/transaction?apptoken="+appToken+"&user_id="+user_id+"&type="+type+"&start="+start_page);
                    Log.e("Sending data",SharePrfeManager.getInstance(Statement.this).mGetBaseUrl()+"api/android/transaction?apptoken="+appToken+"&user_id="+user_id+"&type="+type+"&start="+start_page);
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
                if (swiprefresh_recharge_statement.isRefreshing()) {
                    swiprefresh_recharge_statement.setRefreshing(false);
                }

                if (!result.equals(""))
                {

                    try
                    {
                        JSONObject jsonObject=new JSONObject(result);
                        if (jsonObject.has("statuscode"))
                        {
                            if (jsonObject.getString("statuscode").equalsIgnoreCase("txn"))
                            {
                                JSONArray jsonArray=jsonObject.getJSONArray("data");
                                for (int i=0; i<jsonArray.length(); i++)
                                {
                                    JSONObject data=jsonArray.getJSONObject(i);

                                    Items item = new Items();
                                    item.setId(data.getString("id"));
                                    item.setAmount(data.getString("amount"));
                                    item.setBalance(data.getString("previous_balance"));
                                    item.setCharge(data.getString("charge"));
                                    item.setCreated_at(data.getString("created_at"));
                                    item.setNumber(data.getString("number"));
                                    item.setDescription(data.getString("description"));
                                    item.setProfit(data.getString("profit"));
                                    item.setCharge(data.getString("charge"));
                                    item.setTxnid(data.getString("txn_id"));
                                    item.setStatus(data.getString("status"));
                                    item.setActivity(activity);

                                    items.add(item);
                                    cardAdapter.notifyDataSetChanged();
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
                            else
                            {
                                recyclerview_statement.setVisibility(View.GONE);
                                textview_message.setText("Something went wrong");
                                textview_message.setVisibility(View.VISIBLE);
                            }
                        }

                        if (items.size()==0)
                        {
                            textview_message.setText("No records found");
                            recyclerview_statement.setVisibility(View.GONE);
                            textview_message.setVisibility(View.VISIBLE);
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
                    recyclerview_statement.setVisibility(View.GONE);
                    textview_message.setVisibility(View.VISIBLE);
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

        if (DetectConnection.checkInternetConnection(Statement.this))
        {
            mGetStatementData(SharePrfeManager.getInstance(Statement.this).mGetappToken(),SharePrfeManager.getInstance(Statement.this).mGetUserId(),type);

        }
    }


}
