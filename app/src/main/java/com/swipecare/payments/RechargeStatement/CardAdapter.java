package com.swipecare.payments.RechargeStatement;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.swipecare.payments.DetectConnection;
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

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    Context context;
    List<Items>  items;
    ProgressDialog dialog;

    public CardAdapter(Context context,List<Items> items) {
        this.context=context;
        this.items=items;
    }
    @Override
    public int getItemCount() {
        return items==null ?0:items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Items item=items.get(i);
        viewHolder.textview_txnid.setText("Txnid : "+item.getTxnid());
        viewHolder.textview_date.setText("Date : "+item.getCreated_at());
        viewHolder.textview_number.setText("Number : "+item.getNumber());
        viewHolder.textview_amount.setText("Amount : Rs "+item.getAmount());
        viewHolder.textview_profit.setText("Profit : Rs "+item.getProfit());
        viewHolder.textview_charge.setText("Charge : Rs "+item.getCharge());
        viewHolder.textview_desc.setText("Description : "+item.getDescription());
        viewHolder.textview_balance.setText("Balance : Rs "+item.getBalance());
        viewHolder.textview_status.setText(item.getStatus());

        if (item.getStatus().equalsIgnoreCase("success"))
        {
            viewHolder.textview_status.setBackgroundColor(context.getResources().getColor(R.color.green));
        }
        else if (item.getStatus().equalsIgnoreCase("failure")||
                item.getStatus().equalsIgnoreCase("fail")||
                item.getStatus().equalsIgnoreCase("failed"))
        {
            viewHolder.textview_status.setBackgroundColor(context.getResources().getColor(R.color.red));
        }
        else {
            viewHolder.textview_status.setBackgroundColor(context.getResources().getColor(R.color.orange));
        }

        if (i==items.size()-1) {
            if (Statement.last_array_empty) {

            } else {
                ((Statement)context).mCallNextList();
            }
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.statement_item,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textview_txnid,textview_date,textview_number,textview_amount,textview_profit,
                textview_charge,textview_desc,textview_via,textview_balance,textview_status;

        Button button_check_status;
        ViewHolder(View view)
        {
            super(view);
            textview_txnid=view.findViewById(R.id.textview_txnid);
            textview_date=view.findViewById(R.id.textview_date);
            textview_number=view.findViewById(R.id.textview_number);
            textview_amount=view.findViewById(R.id.textview_amount);
            textview_profit=view.findViewById(R.id.textview_profit);
            textview_charge=view.findViewById(R.id.textview_charge);
            textview_desc=view.findViewById(R.id.textview_desc);
            textview_via=view.findViewById(R.id.textview_via);
            textview_balance=view.findViewById(R.id.textview_balance);
            textview_status=view.findViewById(R.id.textview_status);

            button_check_status=view.findViewById(R.id.button_check_status);
            button_check_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (DetectConnection.checkInternetConnection(context)) {
                        mGetLoginData(SharePrfeManager.getInstance(context).mGetappToken(),items.get(getAdapterPosition()).getId(),SharePrfeManager.getInstance(context).mGetUserId(),items.get(getAdapterPosition()).getActivity());
                    }else {
                        Toast.makeText(context, "NO internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private  void  mGetLoginData(final  String appToken,final String txnid,final String user_id,final String activity) {
        class getJSONData extends AsyncTask<String, String, String> {


            HttpURLConnection urlConnection;

            String sendingURL="";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog=new ProgressDialog(context);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);

                if (activity.equalsIgnoreCase("recharge")) {
                    sendingURL=SharePrfeManager.getInstance(context).mGetBaseUrl()+"api/android/recharge/status?apptoken="+appToken+"&txnid="+txnid+"&user_id="+user_id;
                } else {
                    sendingURL=SharePrfeManager.getInstance(context).mGetBaseUrl()+"api/android/billpay/status?apptoken="+appToken+"&txnid="+txnid+"&user_id="+user_id;
                }
            }
            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    Log.e("url", sendingURL);
                    URL url = new URL(sendingURL);
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

                if (!result.equals("")) {
                    String status="";
                    String message="";
                    String refno="";
                    try {
                        JSONObject jsonObject=new JSONObject(result);

                        if (jsonObject.has("statuscode")) {
                            status = jsonObject.getString("statuscode");
                        }

                        if (jsonObject.has("txn_status")) {
                            message=jsonObject.getString("txn_status");
                        }else{
                            message="Unknown";
                        }

                        if (jsonObject.has("refno")) {
                            refno = "Operator Ref No. - " + jsonObject.getString("refno");
                        }else{
                            refno = "Status Not Found, Please contact admin support";
                        }

                        final AlertDialog.Builder builder1= new AlertDialog.Builder(context);
                        builder1.setMessage(refno);

                        builder1.setCancelable(false)
                                .setTitle(message)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder1.create();
                        alert.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    mShowResponse("a","Something went wrong");
                }
            }
        }

        getJSONData getJSONData=new getJSONData();
        getJSONData.execute();
    }

    protected void mShowResponse(final String status,final String message)
    {
        final AlertDialog alertDialog;
        LayoutInflater inflater2 = (LayoutInflater)context. getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v2 = inflater2.inflate(R.layout.custome_layout_fund_transfer, null);

        Button button_ok = v2.findViewById(R.id.button_ok);
        ImageView imageview_status_image = v2.findViewById(R.id.imageview_status_image);
        TextView textview_message = v2.findViewById(R.id.textview_message);

        if (status.equalsIgnoreCase("err"))
        {
            imageview_status_image.setImageDrawable(context.getResources().getDrawable(R.drawable.error_icon));
            textview_message.setText(message);
            textview_message.setTextColor(context.getResources().getColor(R.color.orange));
        }
        else if (status.equalsIgnoreCase("txn"))
        {
            imageview_status_image.setImageDrawable(context.getResources().getDrawable(R.drawable.success));
            textview_message.setText(message);
            textview_message.setTextColor(context.getResources().getColor(R.color.green));
        }
        else
        {
            imageview_status_image.setImageDrawable(context.getResources().getDrawable(R.drawable.error_icon));
            textview_message.setText(message);
            textview_message.setTextColor(context.getResources().getColor(R.color.orange));
        }


        final AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
        builder2.setCancelable(false);

        builder2.setView(v2);

        alertDialog = builder2.create();
        button_ok.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }


}
