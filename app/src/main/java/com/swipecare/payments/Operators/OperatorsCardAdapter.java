package com.swipecare.payments.Operators;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.swipecare.payments.bbps.Gas;
import com.swipecare.payments.recharges.DTH;
import com.swipecare.payments.recharges.Electricity;
import com.swipecare.payments.recharges.MobileRecharge;
import com.swipecare.payments.R;

import java.util.List;


/**
 * Created by admin on 3/29/2018.
 */

public class OperatorsCardAdapter extends RecyclerView.Adapter<OperatorsCardAdapter.ViewHolder>{

    Context context;
    List<Operators_Items> operators_items;
    public OperatorsCardAdapter(Context context, List<Operators_Items> operators_items)
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
        final Operators_Items item=operators_items.get(position);
        holder.textview_operator.setText(item.getOperator_name());

        holder.textview_capital_latter_operator.setVisibility(View.VISIBLE);
        holder.textview_capital_latter_operator.setText(item.getOperator_name().substring(0,1).toUpperCase());
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView textview_operator,textview_capital_latter_operator;
        ViewHolder(View view)
        {
            super(view);

            textview_operator=(TextView)view.findViewById(R.id.textview_operator);
            textview_capital_latter_operator=(TextView)view.findViewById(R.id.textview_capital_latter_operator);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    Operators_Items item=operators_items.get(position);

                    if (OperatorsFragment.activity_name.equals("mobile"))
                    {
                        ((MobileRecharge)context).mCloseFragment();
                        ((MobileRecharge)context).getData(item.getOperator_id(),item.getOperator_name());
                    }
                    else if (OperatorsFragment.activity_name.equals("dth"))
                    {
                        ((DTH)context).mCloseFragment();
                        ((DTH)context).GetData(item.getOperator_id(),item.getOperator_name());
                    }
                    else if (OperatorsFragment.activity_name.equals("electricity"))
                    {
                        ((Electricity)context).mCloseFragment();
                        ((Electricity)context).GetData(item.getOperator_id(),item.getOperator_name());
                    }
                    else if (OperatorsFragment.activity_name.equals("gass"))
                    {
                        ((Gas)context).mCloseFragment();
                        ((Gas)context).GetData(item.getOperator_id(),item.getOperator_name());
                    }
                }
            });
        }
    }

    public void UpdateList(List<Operators_Items> item)
    {
        operators_items=item;
        notifyDataSetChanged();
    }

}
