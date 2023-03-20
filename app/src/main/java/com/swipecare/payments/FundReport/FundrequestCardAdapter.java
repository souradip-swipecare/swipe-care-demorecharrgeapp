package com.swipecare.payments.FundReport;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swipecare.payments.R;

import java.util.List;

public class FundrequestCardAdapter extends RecyclerView.Adapter<FundrequestCardAdapter.ViewHolder> {

    Context context;
    String type, statement;
    List<fundrequestReportItems> AEPSRequestItems;
    public FundrequestCardAdapter(Context context, List<fundrequestReportItems> AEPSRequestItems)
    {
        this.context=context;
        this.AEPSRequestItems = AEPSRequestItems;
    }


    @Override
    public int getItemCount() {
        return AEPSRequestItems ==null?0: AEPSRequestItems.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        fundrequestReportItems items= AEPSRequestItems.get(i);
        statement = items.getStatementtype();
        if (statement.equals("fundrequest")) {
            viewHolder.textview_amount.setText(items.getAmount());
            viewHolder.textview_date.setText(items.getCreated_at());
            viewHolder.textview_status.setText(items.getStatus());
            viewHolder.textview_number.setText("Acc. No. : " + items.getAccount());
            viewHolder.textview_mobile.setText("Bank : " + items.getBank());
            viewHolder.textview_refno.setText("Ref. No. : " + items.getType());
        }else{
            viewHolder.textview_amount.setText(items.getAmount());
            viewHolder.textview_date.setText(items.getCreated_at());
            viewHolder.textview_status.setText(items.getStatus());
            type = items.getType();
            if(type.equals("bank")) {
                viewHolder.textview_number.setText("Acc. No. : " + items.getAccount());
                viewHolder.textview_mobile.setText("Bank : " + items.getBank());
                viewHolder.textview_refno.setText("Ifsc : " + items.getIfsc());
            }else{
                viewHolder.textview_number.setText("");
                viewHolder.textview_mobile.setText("Move To Wallet");
                viewHolder.textview_refno.setText("");
            }
        }

        if (items.getStatus().equalsIgnoreCase("success") || items.getStatus().equalsIgnoreCase("approved")) {
            viewHolder.textview_status.setTextColor(context.getResources().getColor(R.color.green));
        } else if (items.getStatus().equalsIgnoreCase("rejected")) {
            viewHolder.textview_status.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            viewHolder.textview_status.setTextColor(context.getResources().getColor(R.color.orange));
        }

        if (i==AEPSRequestItems.size()-1) {
            if (FundrequestsReports.last_array_empty) {

            } else {
                ((FundrequestsReports)context).mCallNextList();
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.aeps_request_reports_items,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textview_number,textview_mobile,textview_refno,textview_amount,textview_status,textview_date;
        ViewHolder (View view)
        {
            super(view);
            textview_number=view.findViewById(R.id.st_number);
            textview_mobile=view.findViewById(R.id.st_mobile);
            textview_refno=view.findViewById(R.id.st_refno);
            textview_amount=view.findViewById(R.id.st_amount);
            textview_date=view.findViewById(R.id.st_date);
            textview_status=view.findViewById(R.id.st_status);
        }
    }
}
