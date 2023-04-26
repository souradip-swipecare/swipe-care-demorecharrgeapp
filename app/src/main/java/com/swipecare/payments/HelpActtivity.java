package com.swipecare.payments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelpActtivity extends AppCompatActivity {
    Button button;
    TextView number,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        number = findViewById(R.id.number);
        email = findViewById(R.id.email);
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation_view);

        mGetcompanynews(SharePrfeManager.getInstance(HelpActtivity.this).mGetappToken(), SharePrfeManager.getInstance(HelpActtivity.this).mGetUserId());

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.help);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            switch(item.getItemId())
            {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.help:
                    return true;
                case R.id.my_reports:
                    startActivity(new Intent(getApplicationContext(),Reportactivity.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.profile:
                    startActivity(new Intent(getApplicationContext(),Profileactivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.actionLogout:
                    SharePrfeManager.getInstance(HelpActtivity.this).mLogout();
                    startActivity(new Intent(HelpActtivity.this, Login.class));
                    finish();
                    return true;
            }
            return false;
        });

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String no = "8389887165";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://wa.me/91"+no+"?text=Please guide me with my issue"));
                startActivity(intent);
            }
        });

    }
    private void mGetcompanynews(final String token, final String userid) {
        class getJSONData extends AsyncTask<String, String, String> {


            HttpURLConnection urlConnection;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    URL url = new URL(SharePrfeManager.getInstance(HelpActtivity.this).mGetBaseUrl() + "api/android/company/news?apptoken=" + token + "&user_id=" + userid);
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
                //  Log.e("databgg", result);

                //  System.out.println("balance response : " + result);

                if (!result.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
//                        Log.e("TAG", "onPostExecuteee: "+result );

                        if (jsonObject.has("status")) {
                            if (jsonObject.getString("status").equalsIgnoreCase("txn")) {
                            number.setText(jsonObject.getString("number"));
                                email.setText(jsonObject.getString("email"));
                            }else {
                                number.setText("Somwthing went worng");
                                email.setText("Somwthing went worng");
                            }

                        }else {
                            number.setText("Somwthing went worng");
                            email.setText("Somwthing went worng");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {

                }
            }
        }

        getJSONData getJSONData = new getJSONData();
        getJSONData.execute();
    }

}
