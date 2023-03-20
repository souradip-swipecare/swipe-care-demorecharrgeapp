package com.swipecare.payments.MoneyTransfer.TransactionReciptDetails;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.swipecare.payments.MoneyTransfer.Beneficiary_Items;
import com.swipecare.payments.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoneyTransactionReciept extends AppCompatActivity {


    Beneficiary_Items beneficiary_items;

    TextView textview_name,textview_mobile,textview_benename,textview_account,textview_bank_name,textview_ifsc;

    RecyclerView recyclerview_receipt;
    List<TXNRItems> txnrItems;
    TXNCardAdapter txnCardAdapter;

    String txndata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_testing);

        beneficiary_items=(Beneficiary_Items) getIntent().getExtras().getSerializable("bene");

        textview_name=findViewById(R.id.textview_name);
        textview_mobile=findViewById(R.id.textview_mobile);
        textview_benename=findViewById(R.id.textview_benename);
        textview_account=findViewById(R.id.textview_account);
        textview_bank_name=findViewById(R.id.textview_bank_name);
        textview_ifsc=findViewById(R.id.textview_ifsc);
        recyclerview_receipt=findViewById(R.id.recyclerview_receipt);


        textview_name.setText("Name : "+beneficiary_items.getSendername());
        textview_mobile.setText("Mobile : "+beneficiary_items.getSendernumber());
        textview_benename.setText("Bene Name : "+beneficiary_items.getBenename());
        textview_account.setText("A/c : "+beneficiary_items.getBeneaccount());
        textview_bank_name.setText("Bank : "+beneficiary_items.getBenebank());
        textview_ifsc.setText("IFSC : "+beneficiary_items.getBeneifsc());


        recyclerview_receipt.setLayoutManager(new LinearLayoutManager(MoneyTransactionReciept.this));
        txnrItems=new ArrayList<>();
        txnCardAdapter=new TXNCardAdapter(MoneyTransactionReciept.this,txnrItems);
        recyclerview_receipt.setAdapter(txnCardAdapter);

        txndata=getIntent().getStringExtra("txndata");


        textview_benename.setText("Bene Name : "+beneficiary_items.getBenename());
        textview_account.setText("A/c "+beneficiary_items.getBeneaccount());
        textview_bank_name.setText("Bank : "+beneficiary_items.getBenebank());
        textview_ifsc.setText("IFSC : "+beneficiary_items.getBeneifsc());

        mShowReceipt(txndata);

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

    protected void  mShowReceipt(final String response)
    {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i=0; i<jsonArray.length(); i++)
            {
                JSONObject data=jsonArray.getJSONObject(i);

                TXNRItems items=new TXNRItems();
                items.setAmount(data.getString("amount"));

                if (data.has("status")) {
                    items.setStatuscode(data.getString("status"));
                }

                items.setRrn(data.getString("rrn"));
                items.setMessage(data.getString("message"));
                txnrItems.add(items);
                txnCardAdapter.notifyDataSetChanged();

            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }
}
