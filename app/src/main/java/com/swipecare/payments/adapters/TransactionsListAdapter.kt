package com.swipecare.payments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swipecare.payments.databinding.ItemMiniStatementTransactionBinding
import com.swipecare.payments.model.Transaction


class TransactionsListAdapter() :
    ListAdapter<Transaction, TransactionsListAdapter.TransactionListViewHolder>(
        DiffUtilTransactionCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListViewHolder {
        return TransactionListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TransactionListViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    class TransactionListViewHolder private constructor(private var binding: ItemMiniStatementTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.transactionDate.text = "Date: ${transaction.date}"
            binding.description.text = transaction.description
            binding.transactionAmount.text = "₹ ${transaction.amount}"
            binding.transactionType.text =
                if (transaction.txnType == "C") "Credit" else if (transaction.txnType == "D") "Debit" else transaction.txnType
        }

        companion object {
            fun from(parent: ViewGroup): TransactionListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMiniStatementTransactionBinding.inflate(layoutInflater, parent, false)
                return TransactionListViewHolder(binding)
            }
        }
    }
}

class DiffUtilTransactionCallBack : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }
}