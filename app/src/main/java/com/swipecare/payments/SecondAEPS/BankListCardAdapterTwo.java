package com.swipecare.payments.SecondAEPS;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.swipecare.payments.AEPSWithdrawal;
import com.swipecare.payments.R;

import java.util.List;


public class BankListCardAdapterTwo extends RecyclerView.Adapter<BankListCardAdapterTwo.ViewHolder>{

    Context context;
    List<BankListItems> operators_items;
    public BankListCardAdapterTwo(Context context, List<BankListItems> operators_items)
    {
        this.context=context;
        this.operators_items=operators_items;
    }

    @Override
    public int getItemCount() {
        return operators_items==null ? 0: operators_items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.operator_items,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BankListItems item=operators_items.get(position);
        holder.textview_operator.setText(item.getBank());

        holder.textview_capital_latter_operator.setVisibility(View.VISIBLE);
        holder.textview_capital_latter_operator.setText(item.getBank().substring(0,1).toUpperCase());
        holder.imageview_operator_icon.setVisibility(View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageview_operator_icon;
        TextView textview_operator,textview_capital_latter_operator;
        ViewHolder(View view)
        {
            super(view);
            imageview_operator_icon=(ImageView)view.findViewById(R.id.imageview_operator_icon);
            textview_operator=(TextView)view.findViewById(R.id.textview_operator);
            textview_capital_latter_operator=(TextView)view.findViewById(R.id.textview_capital_latter_operator);

            view.setOnClickListener(view1 -> {
                int position=getAdapterPosition();
                BankListItems item=operators_items.get(position);

                ((AEPSWithdrawal)context).mGetBank(item.getId(),item.getBank(),item.getBankiin());
                AEPSWithdrawal.alertDialog_for_bank.dismiss();
            });
        }
    }

    public void UpdateList(List<BankListItems> item)
    {
        operators_items=item;
        notifyDataSetChanged();
    }
}

