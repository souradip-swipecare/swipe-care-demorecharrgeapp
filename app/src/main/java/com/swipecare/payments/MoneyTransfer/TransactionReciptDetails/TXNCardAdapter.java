package com.swipecare.payments.MoneyTransfer.TransactionReciptDetails;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swipecare.payments.R;

import java.util.List;

public class TXNCardAdapter extends RecyclerView.Adapter<TXNCardAdapter.ViewHolder> {



    Context context;
    List<TXNRItems> txnrItems;

    public TXNCardAdapter (Context context,List<TXNRItems> txnrItems)
    {
        this.context=context;
        this.txnrItems=txnrItems;
    }
    @Override
    public int getItemCount() {
        return txnrItems==null ? 0:txnrItems.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        TXNRItems item=txnrItems.get(i);

        viewHolder.textview_amount.setText("\u20B9 : "+item.getAmount());
        viewHolder.textview_status.setText(item.getMessage());
        viewHolder.textview_rrn.setText("RRN : "+item.getRrn());


        if (item.getStatuscode().equalsIgnoreCase("success")) {
            viewHolder.textview_status.setTextColor(context.getResources().getColor(R.color.green));
        } else if (item.getStatuscode().equalsIgnoreCase("failed"))
        {
            viewHolder.textview_status.setTextColor(context.getResources().getColor(R.color.red));
        } else
        {
            viewHolder.textview_status.setTextColor(context.getResources().getColor(R.color.orange));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(context).inflate(R.layout.receipt_items,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView textview_amount,textview_status,textview_rrn;
        ViewHolder(View view)
        {
            super(view);
            textview_amount=view.findViewById(R.id.textview_amount);
            textview_status=view.findViewById(R.id.textview_status);
            textview_rrn=view.findViewById(R.id.textview_rrn);
        }
    }
}
