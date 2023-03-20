package com.swipecare.payments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {


    Button button_login;
    ProgressDialog dialog;

    EditText edittext_username, edittext_password;

    RelativeLayout rl_message;
    TextView textview_message, textview_forgot_password;

    AlertDialog alertDialog = null;

    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //to hide the title
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mitralogin);

        edittext_username = findViewById(R.id.edittext_username);
        edittext_password = findViewById(R.id.edittext_password);
        rl_message = findViewById(R.id.rl_message);
        textview_message = findViewById(R.id.textview_message);
        textview_forgot_password = findViewById(R.id.textview_forgot_password);

        button_login = findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(Login.this)) {
                    if (edittext_username.getText().toString().equals("")) {
                        textview_message.setText("Please enter username");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Login.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    } else if (edittext_username.getText().toString().length() < 10) {
                        textview_message.setText("Please enter v valid username");
                        rl_message.setVisibility(View.VISIBLE);
                        rl_message.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animation a = AnimationUtils.loadAnimation(Login.this, R.anim.animation_down);
                                rl_message.setAnimation(a);
                                rl_message.clearAnimation();
                                rl_message.setVisibility(View.GONE);
                            }
                        }, 3000);
                    } else {
                        if (edittext_password.getVisibility() != View.VISIBLE) {
                            Animation animate = AnimationUtils.loadAnimation(Login.this, R.anim.right_to_left);
                            animate.setDuration(500);
                            animate.setFillAfter(true);
                            edittext_password.startAnimation(animate);
                            edittext_password.setVisibility(View.VISIBLE);
                            button_login.setText("Login");
                        } else if (edittext_password.getVisibility() == View.VISIBLE && edittext_password.getText().toString().equals("")) {
                            textview_message.setText("Please enter password");
                            rl_message.setVisibility(View.VISIBLE);
                            rl_message.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Animation a = AnimationUtils.loadAnimation(Login.this, R.anim.animation_down);
                                    rl_message.setAnimation(a);
                                    rl_message.clearAnimation();
                                    rl_message.setVisibility(View.GONE);
                                }
                            }, 3000);
                        } else {
                            username = edittext_username.getText().toString();
                            String password = edittext_password.getText().toString();
                            mGetLoginData(username, password);
                        }
                    }
                } else {
                    textview_message.setText("No internet connection");
                    rl_message.setVisibility(View.VISIBLE);
                    rl_message.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation a = AnimationUtils.loadAnimation(Login.this, R.anim.animation_down);
                            rl_message.setAnimation(a);
                            rl_message.clearAnimation();
                            rl_message.setVisibility(View.GONE);
                        }
                    }, 3000);
                }
            }
        });

        textview_forgot_password = findViewById(R.id.textview_forgot_password);
        textview_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(Login.this)) {
                    if (edittext_username.getText().toString().equals("")) {
                        Toast.makeText(Login.this, "Please enter mobile", Toast.LENGTH_SHORT).show();
                    } else if (edittext_username.getText().toString().length() < 10) {
                        Toast.makeText(Login.this, "Please enter a valid mobile no", Toast.LENGTH_SHORT).show();
                    } else {
                        username = edittext_username.getText().toString();
                        mForgotPassword(username);
                    }
                } else {
                    Toast.makeText(Login.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button showHidePassword = findViewById(R.id.show_hide_password);
        showHidePassword.setOnClickListener(v -> {
            if (showHidePassword.getText().toString().equals(getString(R.string.show))) {
                edittext_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                edittext_password.setSelection(edittext_password.length());
                showHidePassword.setText(getString(R.string.hide));
            } else {
                edittext_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                edittext_password.setSelection(edittext_password.length());
                showHidePassword.setText(getString(R.string.show));
            }
        });
    }

    private void mGetLoginData(final String username, final String password) {
        class getJSONData extends AsyncTask<String, String, String> {


            HttpURLConnection urlConnection;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(Login.this);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                 //   Log.e("url", SharePrfeManager.getInstance(Login.this).mGetBaseUrl() + "api/android/auth?mobile=" + username + "&password=" + password);
                    URL url = new URL(SharePrfeManager.getInstance(Login.this).mGetBaseUrl() + "api/android/auth?mobile=" + username + "&password=" + password);
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
              //  Log.e("login response", result);

                if (!result.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("status").equalsIgnoreCase("txn")) {
                            JSONObject userdata = jsonObject.getJSONObject("userdata");
                            String id = userdata.getString("id");
                            String name = userdata.getString("name");
                            String email = userdata.getString("email");
                            String mobile = userdata.getString("mobile");
                            String mainwallet = userdata.getString("mainwallet");
                            String aepsbalance = userdata.getString("aepsbalance");
                            String role_id = userdata.getString("role_id");
                            String parent_id = userdata.getString("parent_id");
                            String status = userdata.getString("status");
                            String company_id = userdata.getString("company_id");
                            String shopname = userdata.getString("shopname");
                            String apptoken = userdata.getString("apptoken");
                            String utiid = userdata.getString("utiid");
                            String utiidtxnid = userdata.getString("utiidtxnid");
                            String utiidstatus = userdata.getString("utiidstatus");
                            String tokenamount = userdata.getString("tokenamount");
                            String account = userdata.getString("account");
                            String bank = userdata.getString("bank");
                            String ifsc = userdata.getString("ifsc");
                            String aepsid = userdata.getString("aepsid");
                            JSONObject active = jsonObject.getJSONObject("company");
                            String upi_status = active.getString("upi_status");
                            String bharat_status = active.getString("bharat_status");



                            SharePrfeManager.getInstance(Login.this).mSaveUserData(username, password, id, name, email, mobile, mainwallet, aepsbalance,
                                    role_id, parent_id, status, company_id, shopname, apptoken, utiid, utiidtxnid, utiidstatus, tokenamount, account, bank, ifsc, aepsid, upi_status, bharat_status);
                            startActivity(new Intent(Login.this, MainActivity.class));
                        } else {
                            Toast.makeText(Login.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    textview_message.setText("Something went wrong");
                    rl_message.setVisibility(View.VISIBLE);
                    rl_message.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation a = AnimationUtils.loadAnimation(Login.this, R.anim.animation_down);
                            rl_message.setAnimation(a);
                            rl_message.clearAnimation();
                            rl_message.setVisibility(View.GONE);
                        }
                    }, 3000);
                }
            }
        }

        getJSONData getJSONData = new getJSONData();
        getJSONData.execute();
    }


    //    for forgot password
    private void mForgotPassword(final String username) {
        class getJSONData extends AsyncTask<String, String, String> {


            HttpURLConnection urlConnection;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(Login.this);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    URL url = new URL(SharePrfeManager.getInstance(Login.this).mGetBaseUrl() + "api/android/auth/reset/request?mobile=" + username);
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


                String myresult = "";
                myresult = result;
                if (!myresult.equals("")) {
                    String status = "";
                    String message = "";
                    try {
                        JSONObject jsonObject = new JSONObject(myresult);
                        if (jsonObject.has("status")) {
                            status = jsonObject.getString("status");
                        }

                        if (jsonObject.has("message")) {
                            message = jsonObject.getString("message");
                        }

                        if (status.equalsIgnoreCase("txn")) {
                            mShowOTPDialog();
                        } else {
                            Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        }

        getJSONData getJSONData = new getJSONData();
        getJSONData.execute();
    }


    protected void mShowOTPDialog() {
        LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v2 = inflater2.inflate(R.layout.custome_alert_dialog_for_enter_otp, null);

        final EditText edittext_otp = v2.findViewById(R.id.edittext_otp);
        final EditText edittext_new_password = v2.findViewById(R.id.edittext_new_password);
        TextView textview_resend_otp = v2.findViewById(R.id.textview_resend_otp);
        Button button_submit = v2.findViewById(R.id.button_submit);
        Button button_cancel = v2.findViewById(R.id.button_cancel);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(Login.this)) {
                    if (edittext_otp.getText().toString().equals("")) {
                        Toast.makeText(Login.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                    } else if (edittext_new_password.getText().toString().equals("")) {
                        Toast.makeText(Login.this, "Please enter new password", Toast.LENGTH_SHORT).show();
                    } else {
                        String otp = edittext_otp.getText().toString();
                        String password = edittext_new_password.getText().toString();
                        mSubmitPassword(username, otp, password);
                    }
                } else {
                    Toast.makeText(Login.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textview_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(Login.this)) {
                    if (edittext_username.getText().toString().equals("")) {
                        Toast.makeText(Login.this, "Please enter mobile", Toast.LENGTH_SHORT).show();
                    } else if (edittext_username.getText().toString().length() < 10) {
                        Toast.makeText(Login.this, "Please enter a valid mobile no", Toast.LENGTH_SHORT).show();
                    } else {
                        username = edittext_username.getText().toString();
                        alertDialog.dismiss();
                        mForgotPassword(username);
                    }
                } else {
                    Toast.makeText(Login.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


        final AlertDialog.Builder builder2 = new AlertDialog.Builder(Login.this);
        builder2.setCancelable(false);

        builder2.setView(v2);

        alertDialog = builder2.create();
        button_cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

        alertDialog.show();
    }


//    /for submit new password

    //    for forgot password
    private void mSubmitPassword(final String username, final String otp, final String password) {
        class getJSONData extends AsyncTask<String, String, String> {


            HttpURLConnection urlConnection;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(Login.this);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    URL url = new URL(SharePrfeManager.getInstance(Login.this).mGetBaseUrl() + "api/android/auth/reset?mobile=" + username + "&token=" + otp + "&password=" + password);
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


                String myresult = "";
                myresult = result;
                if (!myresult.equals("")) {
                    String status = "";
                    String message = "";
                    try {
                        JSONObject jsonObject = new JSONObject(myresult);
                        if (jsonObject.has("status")) {
                            status = jsonObject.getString("status");
                        }

                        if (jsonObject.has("message")) {
                            message = jsonObject.getString("message");
                        }

                        if (status.equalsIgnoreCase("txn")) {
                            if (alertDialog != null && alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }

                            Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        }

        getJSONData getJSONData = new getJSONData();
        getJSONData.execute();
    }


}
