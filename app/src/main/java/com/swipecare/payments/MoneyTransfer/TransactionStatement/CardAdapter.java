package com.swipecare.payments.MoneyTransfer.TransactionStatement;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swipecare.payments.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    Context context;
    List<StatementItems> statementItems;
    public CardAdapter(Context context, List<StatementItems> statementItems)
    {
        this.context=context;
        this.statementItems = statementItems;
    }
    @Override
    public int getItemCount() {
        return statementItems ==null?0: statementItems.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        StatementItems items= statementItems.get(i);
        viewHolder.textview_id.setText("Id : "+items.getId());
        viewHolder.textview_txnid.setText("Txnid : "+items.getTxnid());
        viewHolder.textview_number.setText("Number : "+items.getNumber());
        viewHolder.textview_mobile.setText("Mobile : "+items.getMobile());
        viewHolder.textview_desc.setText("Desc : "+items.getDescription());
        viewHolder.textview_remark.setText("Remark : "+items.getRemark());
        viewHolder.textview_amount.setText("Amount : Rs "+items.getAmount());
        viewHolder.textview_profit.setText("Profit : Rs "+items.getProfit()+", Charge : Rs "+items.getCharge()+", GST : Rs "+items.getGst()+", TDS : Rs "+items.getTds());
        viewHolder.textview_balance.setText("Balance : Rs "+items.getBalance());
        viewHolder.textview_trans_type.setText("Trans Type : "+items.getTrans_type());
        viewHolder.textview_status.setText(items.getStatus());


        if (items.getStatus().equalsIgnoreCase("success"))
        {
            viewHolder.textview_status.setBackgroundColor(context.getResources().getColor(R.color.green));
        }
        else if (items.getStatus().equalsIgnoreCase("failure")||
                items.getStatus().equalsIgnoreCase("fail")||
                items.getStatus().equalsIgnoreCase("failed"))
        {
            viewHolder.textview_status.setBackgroundColor(context.getResources().getColor(R.color.red));
        }
        else {
            viewHolder.textview_status.setBackgroundColor(context.getResources().getColor(R.color.orange));
        }


        if (i==statementItems.size()-1) {
            if (DMTStatement.last_array_empty) {

            } else {
                ((DMTStatement)context).mCallNextList();
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pancard_statement_items,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textview_id,textview_txnid,textview_number,textview_mobile,textview_desc,
                textview_remark,textview_amount,textview_via,textview_profit,textview_balance,textview_trans_type,textview_status;
        ViewHolder(View view)
        {
            super(view);
            textview_id=view.findViewById(R.id.textview_id);
            textview_txnid=view.findViewById(R.id.textview_txnid);
            textview_number=view.findViewById(R.id.textview_number);
            textview_mobile=view.findViewById(R.id.textview_mobile);
            textview_desc=view.findViewById(R.id.textview_desc);
            textview_remark=view.findViewById(R.id.textview_remark);
            textview_amount=view.findViewById(R.id.textview_amount);
            textview_profit=view.findViewById(R.id.textview_profit);
            textview_balance=view.findViewById(R.id.textview_balance);
            textview_trans_type=view.findViewById(R.id.textview_trans_type);
            textview_status=view.findViewById(R.id.textview_status);
        }
    }
}
