package com.swipecare.payments;

import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import static com.swipecare.payments.Constants.REQUEST_CODE_SECURITY_SETTINGS;
import static com.swipecare.payments.utils.CheckOnBoardingStatusKt.checkOnboardingStatus;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricManager.Authenticators;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.swipecare.payments.bbps.Gas;
import com.swipecare.payments.menuReports.AccountStatementTransactionsActivity;
import com.swipecare.payments.menuReports.AepsReportActivity;
import com.swipecare.payments.menuReports.PayoutReportActivity;
import com.swipecare.payments.payout.SwipePayoutActivity;
import com.swipecare.payments.recharges.MobileRecharge;
import com.swipecare.payments.SecondAEPS.AEPSKotlin;
import com.swipecare.payments.TransactionReports.TransactionReports;
import com.swipecare.payments.recharges.DTH;
import com.swipecare.payments.recharges.Electricity;
import com.swipecare.payments.recharges.Mobile_Recharge;
import com.swipecare.payments.recharges.WalletTransferActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;

import androidx.biometric.BiometricPrompt.PromptInfo;
import androidx.biometric.BiometricPrompt;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    NavigationView navigationView;
    DrawerLayout drawer;

    TextView textview_main_balance ;
    TextView textview_aeps_balance;
    TextView today_profit, monthly_profit ;
    LinearLayout nav_wallet_request_reportt,nav_statementt,contact_us;



    // Banking Aeps Services
    CardView cashWithdraw, balanceEnquiry, miniStatement, kyc;
    // Payout Services
    CardView swipePayout, bal_add_online;
    LinearLayout walletTransfer,textview_add;

    LinearLayout topLayoutMainDashboard;
    int INTENT_CODE = 1;

    ProgressDialog dialog;
    SliderLayout mDemoSlider;

    CircleImageView circular_image_profile;
    TextView textview_name, textview_mobile;

    LinearLayout ll_dth, ll_electricity, loadwallet, aepsload;
    LinearLayout prepaidRecharge, postPaidRecharge, dthRecharge,  fastTag, electricityBill, prepaidgas;
    //    NestedScrollView nestedScrollView;
    private static final int BIOMETRIC_ENROLLMENT_RESULT = 5;
    static final int ONBOARDING_STATUS_RESULT = 10;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private PromptInfo promptInfoForAboveApi30;
    private PromptInfo promptInfoForBelowApi30;
    private BiometricManager biometricManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add Biometric/device credential authentication
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // when the user closes the biometric prompt
                Toast.makeText(MainActivity.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
//                Toast.makeText(MainActivity.this, "Authentication successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
//                Toast.makeText(MainActivity.this, "Some failure occurred. Please try again", Toast.LENGTH_SHORT).show();
            }
        });

        biometricManager = BiometricManager.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptInfoForAboveApi30 = new PromptInfo.Builder()
                    .setTitle(getString(R.string.enter_device_credentials_or_biometric))
                    .setSubtitle(getString(R.string.unlock, getString(R.string.swipecare)))
                    .setAllowedAuthenticators(Authenticators.BIOMETRIC_STRONG | DEVICE_CREDENTIAL)
                    .build();
            switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    // App can authenticate using biometrics.
                    biometricPrompt.authenticate(promptInfoForAboveApi30);
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE: {
                    Toast.makeText(this, getString(R.string.biometric_hardware_unavailable), Toast.LENGTH_SHORT).show();
                    // TODO: fix this in the future
                    finish();
                    break;
                }
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE: {
                    Toast.makeText(this, getString(R.string.biometric_features_unavailable), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED: {
                    // Prompts the user to create credentials that your app accepts.
                    Toast.makeText(this,
                            getString(R.string.set_screen_lock_or_finger_print),
                            Toast.LENGTH_LONG).show();
                    try {
                        final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                Authenticators.BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                        startActivityForResult(enrollIntent, BIOMETRIC_ENROLLMENT_RESULT);
                    } catch (ActivityNotFoundException e) {
                        // devices below api 30
                        Toast.makeText(this, getString(R.string.set_screen_lock_or_finger_print), Toast.LENGTH_SHORT).show();
                        gotoSecuritySettings(this);
                    } catch (Exception e) {
                        Toast.makeText(this, "error occurred:" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            // for api below 30
            if (!checkBiometricSupport()) {
                // no biometric support on the device
            } else {
                promptInfoForBelowApi30 = new PromptInfo.Builder()
                        .setTitle(getString(R.string.enter_device_credentials_or_biometric))
                        .setSubtitle(getString(R.string.unlock, getString(R.string.swipecare)))
                        .setDeviceCredentialAllowed(true)
                        .build();
                biometricPrompt.authenticate(promptInfoForBelowApi30);
            }
        }

        // Authentication section end
        topLayoutMainDashboard = findViewById(R.id.top_layout_main_dashboard);

        textview_main_balance = findViewById(R.id.textview_main_balance);
        today_profit = findViewById(R.id.today_profit);
        monthly_profit = findViewById(R.id.monthly_profit);


        textview_add = findViewById(R.id.textview_add);
        textview_aeps_balance = findViewById(R.id.textviewaepsbalance);

        cashWithdraw = findViewById(R.id.cash_withdraw);
        balanceEnquiry = findViewById(R.id.balance_enquiry);
        miniStatement = findViewById(R.id.miniStatement);
        kyc = findViewById(R.id.kyc);

//        textview_main_balance.setText("" + SharePrfeManager.getInstance(MainActivity.this).mGetMainBalance());
        //  today_profit.setText("₹ " + SharePrfeManager.getInstance(MainActivity.this).mGetMainBalance());


        prepaidRecharge = findViewById(R.id.prepaid_recharge);
        prepaidRecharge.setOnClickListener(v -> startActivity(
                new Intent(MainActivity.this, MobileRecharge.class)));
        dthRecharge = findViewById(R.id.dth_recharge);
        dthRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DTH.class));
            }
        });
        nav_wallet_request_reportt = findViewById(R.id.nav_wallet_request_reportt);
        nav_wallet_request_reportt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccountStatementTransactionsActivity.class));
            }
        });
        nav_statementt = findViewById(R.id.nav_statementt);
        nav_statementt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TransactionReports.class);
                intent.putExtra("type", "rechargestatement");
                startActivity(intent);
            }
        });
        contact_us = findViewById(R.id.contact_us);
        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String no = SharePrfeManager.getInstance(MainActivity.this).mGetMobile();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://wa.me/91"+no+"?text=Please guide me with my issue"));
                startActivity(intent);
            }
        });
        //        swipeWallet = findViewById(R.id.swipe_wallet);
//        swipeWallet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO:do something like start a new activity on present
//                startActivity(new Intent(MainActivity.this, DTH.class));
//            }
//        });




//        utipan=findViewById(R.id.ll_uti);
//        utipan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, PanCard.class));
//            }
//        });

        bal_add_online = findViewById(R.id.bal_add_online);
        bal_add_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Fundpage.class));
            }
        });

//        nestedScrollView = findViewById(R.id.nested_scroll_view);
//        final boolean[] scrolled = {false};
//        nestedScrollView.smoothScrollTo(topLayoutMainDashboard.getScrollX(), topLayoutMainDashboard.getScrollY());
//        nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
///*                if (oldScrollY < scrollY && (scrollY > 100))  {
//                    // scrolling down
//                    topLayoutMainDashboard.setVisibility(View.GONE);
//                } else {
//                    // scrolling up
//                    if (scrollY < 100 && scrollY > 0) {
//                        topLayoutMainDashboard.setVisibility(View.VISIBLE);
//                    }
//                    Log.i("Hari", "onScrollChange: " + "" + scrollX + " " + scrollY + " " + oldScrollX + " " + oldScrollY);
//                }*/
//            }
//        });

        // Banking Aeps services(ICICI) portion start

        // Payout services portion end


//        cardView = findViewById(R.id.cardView);
//        cardViewTwo = findViewById(R.id.cardViewTwo);
//        cardViewTwo.setOnClickListener(v -> {
//            cardView.setRadius(0);
//            cardView.setCardElevation(0);
//            cardViewTwo.setRadius(35);
//            cardViewTwo.setCardElevation(20);
//        });
//        cardView.setOnClickListener(v -> {
//            cardView.setRadius(35);
//            cardView.setCardElevation(20);
//            cardViewTwo.setRadius(0);
//            cardViewTwo.setCardElevation(0);
//        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hambargicon);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // view pager
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);

        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("1", R.drawable.image1);
        file_maps.put("2", R.drawable.image2);
        file_maps.put("3", R.drawable.image3);
        file_maps.put("4", R.drawable.image4);

        for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        }
        View headerView = navigationView.getHeaderView(0);
        textview_name = headerView.findViewById(R.id.textview_name);
        textview_mobile = headerView.findViewById(R.id.textview_mobile);
//
        textview_name.setText(SharePrfeManager.getInstance(MainActivity.this).mGetName());
        textview_mobile.setText(SharePrfeManager.getInstance(MainActivity.this).mGetMobile());

        // bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.actionLogout) {
            SharePrfeManager.getInstance(MainActivity.this).mLogout();
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_aeps_transaction_reports) {
            Intent intent = new Intent(MainActivity.this, TransactionReports.class);
            intent.putExtra("type", "aepsstatement");
            startActivity(intent);
        } else if (id == R.id.nav_statement) {
            Intent intent = new Intent(MainActivity.this, TransactionReports.class);
            intent.putExtra("type", "rechargestatement");
            startActivity(intent);
        } else if (id == R.id.nav_bill_payment_statement) {
            Intent intent = new Intent(MainActivity.this, TransactionReports.class);
            intent.putExtra("type", "billpaystatement");
            startActivity(intent);
        } else if (id == R.id.nav_dmt_statement) {
            Intent intent = new Intent(MainActivity.this, TransactionReports.class);
            intent.putExtra("type", "dmtstatement");
            startActivity(intent);
        } else if (id == R.id.nav_matm_statement) {
            Intent intent = new Intent(MainActivity.this, TransactionReports.class);
            intent.putExtra("type", "matmstatement");
            startActivity(intent);
        } else if (id == R.id.nav_aeps_wallet_statement) {
            Intent intent = new Intent(MainActivity.this, AepsReportActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_wallet_request_report) {
            Intent intent = new Intent(MainActivity.this, AccountStatementTransactionsActivity.class);
//            intent.putExtra("type", "fundrequest");
        } else if (id == R.id.payout_report) {
            Intent intent = new Intent(this, PayoutReportActivity.class);
            startActivity(intent);
            return true;
        }  else if (id == R.id.actionLogout) {
            SharePrfeManager.getInstance(MainActivity.this).mLogout();
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            return true;
        }else if (id == R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (id == R.id.my_reports) {

        } else if (id == R.id.wallet) {

        } else if (id == R.id.notifications) {

        } else if (id == R.id.help) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_CODE) {
            if (requestCode == RESULT_OK) {
                Toast.makeText(this, data.getStringExtra("StatusCode") + "\n" + data.getStringExtra("Message"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, data.getStringExtra("StatusCode") + "\n" + data.getStringExtra("Message"), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == ONBOARDING_STATUS_RESULT && resultCode == RESULT_OK && data != null) {
            boolean status = data.getBooleanExtra("status", false);
            int response = data.getIntExtra("response", 0);
            String message = data.getStringExtra("message");
            String detailedResponse = "Status: " + status + ",  " +
                    "Response: " + response + ", " +
                    "Message: " + message;
            Toast.makeText(this, detailedResponse, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGetLoginData(SharePrfeManager.getInstance(MainActivity.this).mGetUsername(), SharePrfeManager.getInstance(MainActivity.this).mGetPassword());
        mGetprofit(SharePrfeManager.getInstance(MainActivity.this).mGetappToken(), SharePrfeManager.getInstance(MainActivity.this).mGetUserId());
        mGetBalance(SharePrfeManager.getInstance(MainActivity.this).mGetappToken(), SharePrfeManager.getInstance(MainActivity.this).mGetUserId());

        checkBiometricSupport();
    }

    private void mGetprofit(final String token, final String userid) {
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
                    URL url = new URL(SharePrfeManager.getInstance(MainActivity.this).mGetBaseUrl() + "api/android/memberstats?apptoken=" + token + "&user_id=" + userid);
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
                        if (jsonObject.has("status")) {
                            if (jsonObject.getString("status").equalsIgnoreCase("txn")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                // TODO: change this later. don't forget
                                today_profit.setText(" ₹ " + data.getString("today"));
                                monthly_profit.setText(" ₹ " + data.getString("month"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        getJSONData getJSONData = new getJSONData();
        getJSONData.execute();
    }

    private void mGetBalance(final String token, final String userid) {
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
                    URL url = new URL(SharePrfeManager.getInstance(MainActivity.this).mGetBaseUrl() + "api/android/getbalance?apptoken=" + token + "&user_id=" + userid);
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
                        if (jsonObject.has("status")) {
                            if (jsonObject.getString("status").equalsIgnoreCase("txn")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                // TODO: change this later. don't forget
                                textview_main_balance.setText(" ₹ " + data.getString("mainwallet"));
                                Log.e("TAG", "onPostExecute: "+textview_main_balance );
                                // monthly_profit.setText(" ₹ " + data.getString("month"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        getJSONData getJSONData = new getJSONData();
        getJSONData.execute();
    }




    private void mGetLoginData(final String username, final String password) {
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
                    URL url = new URL(SharePrfeManager.getInstance(MainActivity.this).mGetBaseUrl() + "api/android/auth?mobile=" + username + "&password=" + password);
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

                //   Log.e("data", result);

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
                            String status = userdata.getString("status_id");
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




                            SharePrfeManager.getInstance(MainActivity.this).mSaveUserData(username, password, id, name, email, mobile, mainwallet, aepsbalance,
                                    role_id, parent_id, status, company_id, shopname, apptoken, utiid, utiidtxnid, utiidstatus, tokenamount, account, bank, ifsc, aepsid,upi_status,bharat_status);

                            mGetBalance(SharePrfeManager.getInstance(MainActivity.this).mGetappToken(), SharePrfeManager.getInstance(MainActivity.this).mGetUserId());

                        } else if (jsonObject.getString("status").equalsIgnoreCase("txn") && jsonObject.getString("message").equalsIgnoreCase("Username and Password is incorrect")) {
                            SharePrfeManager.getInstance(MainActivity.this).mLogout();
                            startActivity(new Intent(MainActivity.this, Login.class));
                            finish();
                            Toast.makeText(MainActivity.this, "Login credentials is changed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        }

        getJSONData getJSONData = new getJSONData();
        getJSONData.execute();
    }


    @Override
    protected void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    private void gotoSecuritySettings(@NonNull AppCompatActivity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            activity.startActivityForResult(intent, REQUEST_CODE_SECURITY_SETTINGS);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkBiometricSupport() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (!keyguardManager.isDeviceSecure()) {
            Toast.makeText(this, "Fingerprint authentication is not enabled in settings", Toast.LENGTH_SHORT).show();
            gotoSecuritySettings(this);
            return false;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    "Fingerprint authentication permission not enabled",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return true;
        } else {
            return false;
        }
    }
}
