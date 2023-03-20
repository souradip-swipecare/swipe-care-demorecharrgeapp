package com.swipecare.payments.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swipecare.payments.databinding.ItemRechargePlanBinding
import com.swipecare.payments.model.Plan
import com.swipecare.payments.recharges.MobileRechargePlansActivity

class RechargePlansListAdapter(
    private val onClickListener: OnclickListener,
    private val activity: MobileRechargePlansActivity
) :
    ListAdapter<Plan, RechargePlansListAdapter.RechargePlansViewHolder>(
        DiffUtilRechargeCallBack()) {
    lateinit var binding: ItemRechargePlanBinding
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RechargePlansViewHolder = RechargePlansViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: RechargePlansViewHolder,
        position: Int,
    ) {
        Log.i("Plan", "onBindViewHolder: ${getItem(position)}")
        holder.bind(getItem(position),onClickListener,activity)
    }

    class RechargePlansViewHolder private constructor(private var binding: ItemRechargePlanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(planData: Plan,onClickListener: OnclickListener, activity: MobileRechargePlansActivity) {
            binding.planValidity.text = "Validity: ${planData.validity}"
            binding.planAmount.text = "\u20B9 ${planData.price}"
            binding.planDescription.text = planData.planDescription
            binding.planAmount.setOnClickListener {
                onClickListener.onClick(planData,activity)
            }
        }

        companion object {
            fun from(parent: ViewGroup): RechargePlansViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRechargePlanBinding.inflate(layoutInflater, parent, false)
                return RechargePlansViewHolder(binding)
            }
        }
    }

    class OnclickListener(val clickListener: (rechargePlan: Plan, activity: MobileRechargePlansActivity) -> Unit) {
        fun onClick(rechargePlan: Plan, activity: MobileRechargePlansActivity) = clickListener(rechargePlan,activity)
    }
}

class DiffUtilRechargeCallBack : DiffUtil.ItemCallback<Plan>() {
    override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
        return oldItem == newItem
    }
}