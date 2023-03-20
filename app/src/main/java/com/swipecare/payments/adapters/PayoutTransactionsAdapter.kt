package com.swipecare.payments.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swipecare.payments.R
import com.swipecare.payments.databinding.ItemPayoutReportBinding
import com.swipecare.payments.model.menuReports.payoutReport.BankTransaction

class PayoutTransactionsAdapter(val resources: Resources) :
    ListAdapter<BankTransaction, PayoutTransactionsAdapter.BankTransactionsViewHolder>(
        DiffUtilBankTransactionCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankTransactionsViewHolder {
        return BankTransactionsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BankTransactionsViewHolder, position: Int) {
        val bankTransaction = getItem(position)
        holder.bind(resources,bankTransaction)
    }

    class BankTransactionsViewHolder private constructor(private var binding: ItemPayoutReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resources: Resources,bankTransaction: BankTransaction) {
            binding.status.text = "${bankTransaction.status}"
            binding.fundAccountId.text = "${bankTransaction.fundAccountId}"
            binding.mode.text = "${bankTransaction.mode}"
            binding.amount.text = "${bankTransaction.amount}"
            binding.charge.text = "${bankTransaction.charge}"
            binding.utr.text = "${bankTransaction.utr}"
            binding.payoutId.text = "${bankTransaction.payoutId}"
            binding.createdAt.text = "${bankTransaction.createdAt}"
            binding.userName.text = "${bankTransaction.userName}"
            binding.accountNumber.text =
                "${bankTransaction.accountDetails.accountNumber}"
            binding.ifsc.text = "${bankTransaction.accountDetails.ifsc}"

            when(bankTransaction.status.lowercase())  {
                "processing","pending" -> binding.cardView9.setCardBackgroundColor(resources.getColor(R.color.orange))
                "failed" -> binding.cardView9.setCardBackgroundColor(resources.getColor(R.color.red))
                else -> binding.cardView9.setCardBackgroundColor(resources.getColor(R.color.colorGreenLight))
            }
        }

        companion object {
            fun from(parent: ViewGroup): BankTransactionsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPayoutReportBinding.inflate(layoutInflater, parent, false)
                return BankTransactionsViewHolder(binding)
            }
        }
    }
}

class DiffUtilBankTransactionCallBack : DiffUtil.ItemCallback<BankTransaction>() {
    override fun areItemsTheSame(oldItem: BankTransaction, newItem: BankTransaction): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: BankTransaction, newItem: BankTransaction): Boolean {
        return oldItem == newItem
    }
}