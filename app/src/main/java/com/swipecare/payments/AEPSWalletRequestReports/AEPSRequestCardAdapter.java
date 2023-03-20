package com.swipecare.payments.AEPSWalletRequestReports;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swipecare.payments.R;

import java.util.List;

public class AEPSRequestCardAdapter extends RecyclerView.Adapter<AEPSRequestCardAdapter.ViewHolder> {

    Context context;
    List<AEPSRequestItems> AEPSRequestItems;
    public AEPSRequestCardAdapter(Context context, List<AEPSRequestItems> AEPSRequestItems)
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
        AEPSRequestItems items= AEPSRequestItems.get(i);
        viewHolder.textview_id.setText("Id : "+items.getId());
        viewHolder.textview_amount.setText("Amount : Rs "+items.getAmount());
        viewHolder.textview_date.setText("Date : "+items.getCreated_at());
        viewHolder.textview_type.setText("Type : "+items.getType());
        viewHolder.textview_remark.setText("Remark : "+items.getRemark());
        viewHolder.textview_status.setText(items.getStatus());

        if (items.getStatus().equalsIgnoreCase("approved"))
        {
            viewHolder.textview_status.setBackgroundColor(context.getResources().getColor(R.color.green));
        }
        else if (items.getStatus().equalsIgnoreCase("rejected"))
        {
            viewHolder.textview_status.setBackgroundColor(context.getResources().getColor(R.color.red));
        }
        else {
            viewHolder.textview_status.setBackgroundColor(context.getResources().getColor(R.color.orange));
        }

        if (i==AEPSRequestItems.size()-1) {
            if (AEPSRequests.last_array_empty) {

            } else {
                ((AEPSRequests)context).mCallNextList();
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
        TextView textview_id,textview_amount,textview_type,textview_date,textview_remark,textview_status;
        ViewHolder (View view)
        {
            super(view);
            textview_id=view.findViewById(R.id.textview_id);
            textview_amount=view.findViewById(R.id.textview_amount);
            textview_type=view.findViewById(R.id.textview_type);
            textview_date=view.findViewById(R.id.textview_date);
            textview_remark=view.findViewById(R.id.textview_remark);
            textview_status=view.findViewById(R.id.textview_status);

        }
    }
}
