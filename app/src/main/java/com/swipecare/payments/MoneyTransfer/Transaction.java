package com.swipecare.payments.MoneyTransfer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.swipecare.payments.DetectConnection;
import com.swipecare.payments.MoneyTransfer.TransactionReciptDetails.MoneyTransactionReciept;
import com.swipecare.payments.R;
import com.swipecare.payments.SharePrfeManager;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Transaction extends AppCompatActivity {
    TextView sendernumber,benenametext,accountnumber,bankname,sendername, senderName;

    EditText editamount;
    Button sendbutton;
    ProgressDialog dialog;
    String beneid, mobile,amount,txntype,name,benename,beneaccount,benebank,beneifsc,myJSON;
    Beneficiary_Items beneficiary_items;

    RadioGroup radioGroup;
    RadioButton radiobutton_imps,radiobutton_neft, TxnType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sendernumber= findViewById(R.id.sendernumber);
        sendername= findViewById(R.id.sendername);
        senderName= findViewById(R.id.senderName);
        benenametext= findViewById(R.id.benenametext);
        accountnumber= findViewById(R.id.accountnumber);
        bankname= findViewById(R.id.bankname);
        editamount= findViewById(R.id.editamount);
        radioGroup = findViewById(R.id.radioGroup);
        radiobutton_imps = findViewById(R.id.radiobutton_imps);
        radiobutton_neft = findViewById(R.id.radiobutton_neft);

        radiobutton_imps.setChecked(true);
        beneficiary_items=(Beneficiary_Items) getIntent().getExtras().getSerializable("DATA");
        benenametext.setText(beneficiary_items.getBenename());
        bankname.setText(beneficiary_items.getBenebank()+" ("+beneficiary_items.getBeneifsc()+")");
        accountnumber.setText(beneficiary_items.getBeneaccount());
        sendernumber.setText(beneficiary_items.getSendernumber());
        sendername.setText(beneficiary_items.getSendername());
        senderName.setText("Money transfer to "+ beneficiary_items.getSendername());

        sendbutton= findViewById(R.id.sendbutton);
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(Transaction.this))
                {
                    int inputAmount = Integer.parseInt(editamount.getText().toString());
                    if (editamount.getText().toString().equals("")){
                        Toast.makeText(Transaction.this, "Enter amount", Toast.LENGTH_SHORT).show();
                    }else if ( inputAmount < 100 || inputAmount > 25000 ){
                        Toast.makeText(Transaction.this, "Amount should be between 100 to 25000", Toast.LENGTH_SHORT).show();
                    }else{
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        TxnType = findViewById(selectedId);

                        if (TxnType.getText().toString().equals("NEFT")) {
                            txntype = "NEFT";
                        } else {
                            txntype = "IMPS";
                        }

                        beneifsc = beneficiary_items.getBeneifsc();
                        mobile   = beneficiary_items.getSendernumber();
                        beneid   = beneficiary_items.getBeneid();
                        beneaccount = beneficiary_items.getBeneaccount();
                        amount = editamount.getText().toString();
                        name = beneficiary_items.getSendername();
                        benebank = beneficiary_items.getBenebank();
                        benename =beneficiary_items.getBenename().replaceAll(" ","%20");
                        mShowDialog("Please confirm Bank, Account Number and amount, transfer will not reverse at any circumstances.");
                    }
                }else{
                    Toast.makeText(Transaction.this, "No Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void SendMoney()
    {
        class Send extends AsyncTask<String, String, String>
        {
            HttpURLConnection httpURLConnection;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog=new ProgressDialog(Transaction.this);
                dialog.setMessage("Please Wait and don't press the back or Refresh...");
                dialog.show();
                dialog.setCancelable(false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                myJSON=s;
                Log.e("transaction response",s);
                ShowTransferMessage();
                dialog.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {
                StringBuilder result = new StringBuilder();
                try {
                    URL url = new URL(SharePrfeManager.getInstance(Transaction.this).mGetBaseUrl()+"api/android/dmt/transaction?apptoken="+SharePrfeManager.getInstance(Transaction.this).mGetappToken()+"&user_id="+SharePrfeManager.getInstance(Transaction.this).mGetUserId()+"&type=transfer&mobile="+mobile+"&benebank="+benebank+"&beneifsc="+beneifsc+"&beneaccount="+beneaccount+"&txntype="+txntype+"&benename="+benename+"&name="+name+"&amount="+amount+"&beneid="+beneid);

                    Log.e("Sending data",SharePrfeManager.getInstance(Transaction.this).mGetBaseUrl()+"api/android/dmt/transaction?apptoken="+SharePrfeManager.getInstance(Transaction.this).mGetappToken()+"&user_id="+SharePrfeManager.getInstance(Transaction.this).mGetUserId()+"&type=transfer&mobile="+mobile+"&benebank="+benebank+"&beneifsc="+beneifsc+"&beneaccount="+beneaccount+"&txntype="+txntype+"&benename="+benename+"&name="+name+"&amount="+amount+"&beneid="+beneid);

                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    httpURLConnection.disconnect();
                }

                return result.toString();
            }
        }

        Send send=new Send();
        send.execute();
    }

    protected void ShowTransferMessage()
    {
        String status="";
        String message="";
        try
        {
            JSONObject jsonObject=new JSONObject(myJSON);
            Log.e("Log" , myJSON);
            if (jsonObject.has("status")) {
                status = jsonObject.getString("status");
            }else{
                status = "ERR";
            }

            if (jsonObject.has("message")) {
                message = jsonObject.getString("message");
            }else{
                message = "Transaction Failed";
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        if (!status.equals("")) {
            if (status.equalsIgnoreCase("txn")) {
                Intent intent=new Intent(Transaction.this, MoneyTransactionReciept.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("bene",beneficiary_items);
                bundle.putString("txndata",myJSON);
                intent.putExtras(bundle);
                startActivity(intent);

            } else if (status.equalsIgnoreCase("failure") || status.equalsIgnoreCase("fail")) {
                mShowResponse(status,message);
            } else {
                mShowResponse(status,message);
            }
        } else {
            mShowResponse("a","Something went wrong...");
        }
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

    protected void mShowDialog(final String message)
    {
        final AlertDialog.Builder builder1= new AlertDialog.Builder(Transaction.this);
        builder1.setMessage("Account -  "+beneaccount + "\nAmount - \u20B9 "+ amount + "\nNote - " + message) .setTitle("Transfer Confirmation");

        builder1.setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (DetectConnection.checkInternetConnection(Transaction.this)){
                            SendMoney();
                            dialog.dismiss();
                        } else {
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
            textview_message.setText("Rs "+amount+"\n"+message);
            textview_message.setTextColor(getResources().getColor(R.color.orange));
        }
        else if (status.equalsIgnoreCase("txn"))
        {
            imageview_status_image.setImageDrawable(getResources().getDrawable(R.drawable.success));
            textview_message.setText("Rs "+amount+"\n"+message);
            textview_message.setTextColor(getResources().getColor(R.color.green));
        }
        else
        {
            imageview_status_image.setImageDrawable(getResources().getDrawable(R.drawable.error_icon));
            textview_message.setText("Rs "+amount+"\n"+message);
            textview_message.setTextColor(getResources().getColor(R.color.orange));
        }


        final AlertDialog.Builder builder2 = new AlertDialog.Builder(Transaction.this);
        builder2.setCancelable(false);

        builder2.setView(v2);

        alertDialog = builder2.create();
        button_ok.setOnClickListener(new View.OnClickListener() {
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
}
