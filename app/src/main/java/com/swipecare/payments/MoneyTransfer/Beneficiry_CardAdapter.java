package com.swipecare.payments.MoneyTransfer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

/**
 * Created by Basant on 6/10/2017.
 */

public class Beneficiry_CardAdapter extends RecyclerView.Adapter<Beneficiry_CardAdapter.ViewHolder>{
    List<Beneficiary_Items> beneficiary_items;
    private Context context;

    String myJSON;
    ProgressDialog dialog;
    String message;

    AlertDialog alertDialog;
    String type="";

    String mobileValue="", beneaccountValue="";

    public Beneficiry_CardAdapter(Context context, List<Beneficiary_Items> beneficiary_items)
    {
        super();
        this.context=context;
        this.beneficiary_items=beneficiary_items;
    }

    @Override
    public int getItemCount() {
        return beneficiary_items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_beneficiary_items,parent, false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Beneficiary_Items items=beneficiary_items.get(position);
        holder.textView_beneficiary_name.setText(items.getBenename());
        holder.textView_acountno.setText(items.getBeneaccount());
        holder.textView_bankname.setText(items.getBenebank());
        holder.textView_ifsc.setText(items.getBeneifsc());
        holder.textView_recepient_id.setText(items.getBeneid());

        holder.button_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Transaction.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("DATA", beneficiary_items.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textView_beneficiary_name;
        public TextView textView_acountno;
        public TextView textView_bankname;
        public TextView textView_ifsc;
        public TextView textView_recepient_id;
        Button button_transfer,button_delete;

        public ViewHolder(View view)
        {
            super(view);
            textView_beneficiary_name= view.findViewById(R.id.textview_beneficiary_name);
            textView_acountno= view.findViewById(R.id.textview_acountno);
            textView_bankname= view.findViewById(R.id.textview_bankname);
            textView_ifsc= view.findViewById(R.id.textView_ifsc);
            textView_recepient_id= view.findViewById(R.id.textView_recepient_id);
            button_transfer= view.findViewById(R.id.button_transfer);
        }
    }

    protected void Delete_Beneficiary(final String mobile_number, final String beneficient_id, final int position)
    {
        class Delete extends AsyncTask<String, String, String>
        {
            HttpURLConnection httpURLConnection;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog=new ProgressDialog(context);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
            }

            @Override
            protected void onPostExecute(String s) {
                myJSON=s;
                Log.e("MY JSON",s);
                super.onPostExecute(s);
                dialog.dismiss();
                ShowDelete_Status(position);
            }

            @Override
            protected String doInBackground(String... params) {
                StringBuilder result = new StringBuilder();
                try {
                    URL url = new URL(SharePrfeManager.getInstance(context).mGetBaseUrl() +"app/v1/delete-beneficiary?recipient_id="+beneficient_id+"&mobile_number="+mobile_number);
                    Log.e("sending data",SharePrfeManager.getInstance(context).mGetBaseUrl() +"app/v1/delete-beneficiary?recipient_id="+beneficient_id+"&mobile_number="+mobile_number);
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

        Delete delete=new Delete();
        delete.execute();
    }

    protected void ShowDelete_Status(final int position)
    {
        String status="";
        try
        {
            JSONObject jsonObject=new JSONObject(myJSON);
            status=jsonObject.getString("status");
            message=jsonObject.getString("message");

            if (status.equalsIgnoreCase("success"))
            {
                beneficiary_items.remove(position);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
            else if (!status.equalsIgnoreCase("success"))
            {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
