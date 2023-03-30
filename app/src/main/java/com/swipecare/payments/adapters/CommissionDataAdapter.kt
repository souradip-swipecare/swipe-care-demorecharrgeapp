package com.swipecare.payments.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swipecare.payments.databinding.ItemCommisionBinding
import com.swipecare.payments.model.commision.CommissionData


class CommissionDataAdapter() :
    ListAdapter<CommissionData, CommissionDataAdapter.CommissionDataViewHolder>(
        DiffUtilCommissionDataCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommissionDataViewHolder {
        return CommissionDataViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CommissionDataViewHolder, position: Int) {
        val commissionDataItem = getItem(position)
        holder.bind(commissionDataItem)
    }

    class CommissionDataViewHolder private constructor(private var binding: ItemCommisionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(commissionData: CommissionData) {
            when {
                commissionData.retailer != null -> {
                    binding.retailer.text = commissionData.retailer.toString()
                }
                commissionData.dristributor != null -> {
                    binding.retailer.text = commissionData.dristributor.toString()
                }
                commissionData.md != null -> {
                    binding.retailer.text = commissionData.md.toString()
                }
                commissionData.whitelabel != null -> {
                    binding.retailer.text = commissionData.whitelabel.toString()
                }
            }

            binding.type.text = commissionData.type
            binding.name.text = commissionData.provider.name
        }

        companion object {
            fun from(parent: ViewGroup): CommissionDataViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCommisionBinding.inflate(layoutInflater, parent, false)
                return CommissionDataViewHolder(binding)
            }
        }
    }
}

class DiffUtilCommissionDataCallBack : DiffUtil.ItemCallback<CommissionData>() {
    override fun areItemsTheSame(oldItem: CommissionData, newItem: CommissionData): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: CommissionData, newItem: CommissionData): Boolean {
        return oldItem == newItem
    }
}