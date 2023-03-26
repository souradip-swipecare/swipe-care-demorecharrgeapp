package com.swipecare.payments.TransactionReports;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swipecare.payments.ChekcomplainActivity;

import com.swipecare.payments.Fundpage;
import com.swipecare.payments.R;

import java.util.List;

public class TransactionReportsCardAdapter  extends RecyclerView.Adapter<TransactionReportsCardAdapter.ViewHolder> {

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
        viewHolder.mainlay.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ChekcomplainActivity.class);
            intent.putExtra("textview_number",items.getNumber());
            intent.putExtra("textview_refno",items.getRefno());
            intent.putExtra("textview_status",items.getStatus());
            intent.putExtra("textview_provider",items.getProvider());
            intent.putExtra("textview_date",items.getCreated_at());
            intent.putExtra("textview_amount",items.getAmount());
            intent.putExtra("txnid",items.getProfit());
            view.getContext().startActivity(intent);
        });

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
        return new ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textview_number,textview_mobile,textview_refno,textview_amount,textview_profit,textview_status,textview_date,textview_provider,check_status;
        LinearLayout mainlay;
        ViewHolder (View view)
        {
            super(view);
            textview_number=view.findViewById(R.id.st_number);
            textview_mobile= view.findViewById(R.id.st_mobile);
            textview_refno = view.findViewById(R.id.st_refno);
            textview_amount = view.findViewById(R.id.st_amount);
            textview_profit = view.findViewById(R.id.txnid);
            textview_date=view.findViewById(R.id.st_date);
            textview_status=view.findViewById(R.id.st_status);
            textview_provider=view.findViewById(R.id.st_provider);
            check_status = view.findViewById(R.id.check_status);
           mainlay = view.findViewById(R.id.mainlay);
        }




    }
}
