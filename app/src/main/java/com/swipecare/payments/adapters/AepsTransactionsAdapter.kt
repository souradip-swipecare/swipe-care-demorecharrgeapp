package com.swipecare.payments.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swipecare.payments.R
import com.swipecare.payments.databinding.ItemAepsReportBinding
import com.swipecare.payments.model.menuReports.aepsReport.AepsReportItem

class AepsTransactionsAdapter(val resources: Resources) :
    ListAdapter<AepsReportItem, AepsTransactionsAdapter.AepsTransactionsViewHolder>(
        DiffUtilAepsTransactionCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AepsTransactionsViewHolder {
        return AepsTransactionsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AepsTransactionsViewHolder, position: Int) {
        val aepsTransaction = getItem(position)
        holder.bind(resources, aepsTransaction)
    }

    class AepsTransactionsViewHolder private constructor(private var binding: ItemAepsReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resources: Resources, aepsTransaction: AepsReportItem) {
            binding.mobile.text = "${aepsTransaction.mobile}"
            binding.aadhar.text = "${aepsTransaction.aadhar}"
            binding.txnId.text = "${aepsTransaction.txnId}"
            binding.bank.text = "${aepsTransaction.bank}"
            binding.payId.text = "${aepsTransaction.payId}"
            binding.status.text = "${aepsTransaction.status}"
            binding.aepsType.text = "${aepsTransaction.aepsType}"
//            binding.type.text = "Type: ${aepsTransaction.type}"
            binding.createdAt.text = "${aepsTransaction.createdAt}"
            binding.charge.text = aepsTransaction.charge.toString()
            binding.balance.text = aepsTransaction.balance.toString()
            binding.refno.text = aepsTransaction.refNo ?: "NA"
            binding.amount.text = aepsTransaction.amount.toString()

            when (aepsTransaction.status.lowercase()) {
                "processing", "pending" -> binding.cardView9.setCardBackgroundColor(resources.getColor(
                    R.color.orange))
                "failed" -> binding.cardView9.setCardBackgroundColor(resources.getColor(R.color.red))
                else -> binding.cardView9.setCardBackgroundColor(resources.getColor(R.color.colorGreenLight))
            }
        }

        companion object {
            fun from(parent: ViewGroup): AepsTransactionsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAepsReportBinding.inflate(layoutInflater, parent, false)
                return AepsTransactionsViewHolder(binding)
            }
        }
    }
}

class DiffUtilAepsTransactionCallBack : DiffUtil.ItemCallback<AepsReportItem>() {
    override fun areItemsTheSame(oldItem: AepsReportItem, newItem: AepsReportItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: AepsReportItem, newItem: AepsReportItem): Boolean {
        return oldItem == newItem
    }
}