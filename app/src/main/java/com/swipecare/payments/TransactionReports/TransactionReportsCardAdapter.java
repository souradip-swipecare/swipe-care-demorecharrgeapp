package com.swipecare.payments.TransactionReports;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swipecare.payments.R;

import java.util.List;

public class TransactionReportsCardAdapter extends RecyclerView.Adapter<TransactionReportsCardAdapter.ViewHolder> {

    Context context
            ;
    List<TransactionReportsItems> transactionReportsItems;
    public TransactionReportsCardAdapter(Context context,List<TransactionReportsItems> transactionReportsItems)
    {
        this.context=context;
        this.transactionReportsItems=transactionReportsItems;
    }


    @Override
    public int getItemCount() {
        return transactionReportsItems==null?0:transactionReportsItems.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        TransactionReportsItems items=transactionReportsItems.get(i);
        viewHolder.textview_number.setText(items.getNumber());
        viewHolder.textview_refno.setText(items.getRefno());
        viewHolder.textview_amount.setText(items.getAmount());
        viewHolder.textview_profit.setText(items.getProfit());
        viewHolder.textview_mobile.setText(items.getMobile());
        viewHolder.textview_date.setText(items.getCreated_at());
        viewHolder.textview_status.setText(items.getStatus());
        viewHolder.textview_provider.setText(items.getProvider());

        if (items.getStatus().equalsIgnoreCase("success"))
        {
            viewHolder.textview_status.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if (items.getStatus().equalsIgnoreCase("failure")||items.getStatus().equalsIgnoreCase("fail")||items.getStatus().equalsIgnoreCase("failed"))
        {
            viewHolder.textview_status.setTextColor(context.getResources().getColor(R.color.red));
        }
        else {
            viewHolder.textview_status.setTextColor(context.getResources().getColor(R.color.orange));
        }

        if (i==transactionReportsItems.size()-1) {
            if (TransactionReports.last_array_empty) {

            } else {
                ((TransactionReports)context).mCallNextList();
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.aeps_transaction_reports_items,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textview_number,textview_mobile,textview_refno,textview_amount,textview_profit,textview_status,textview_date,textview_provider;
        ViewHolder (View view)
        {
            super(view);
            textview_number=view.findViewById(R.id.st_number);
            textview_mobile=view.findViewById(R.id.st_mobile);
            textview_refno=view.findViewById(R.id.st_refno);
            textview_amount=view.findViewById(R.id.st_amount);
            textview_profit=view.findViewById(R.id.st_profit);
            textview_date=view.findViewById(R.id.st_date);
            textview_status=view.findViewById(R.id.st_status);
            textview_provider=view.findViewById(R.id.st_provider);
        }
    }
}
