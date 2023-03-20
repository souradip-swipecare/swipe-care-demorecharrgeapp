package com.swipecare.payments.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swipecare.payments.R
import com.swipecare.payments.databinding.ItemAccountStatementTransactionBinding
import com.swipecare.payments.model.menuReports.payoutReport.AccountStatementTransaction

class AccountStatementTransactionsAdapter(val resources: Resources) :
    ListAdapter<AccountStatementTransaction, AccountStatementTransactionsAdapter.AccountStatementTransactionsViewHolder>(
        DiffUtilAccountStatementTransactionCallBack()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AccountStatementTransactionsViewHolder {
        return AccountStatementTransactionsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AccountStatementTransactionsViewHolder, position: Int) {
        val accountStatementTransaction = getItem(position)
        holder.bind(resources, accountStatementTransaction)
    }

    class AccountStatementTransactionsViewHolder private constructor(private var binding: ItemAccountStatementTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resources: Resources, accountStatementTransaction: AccountStatementTransaction) {
            binding.transType.text = accountStatementTransaction.transactionType
            binding.number.text = accountStatementTransaction.number
            binding.txnid.text = "${accountStatementTransaction.txnId}"
            binding.refno.text = accountStatementTransaction.refNo
            binding.product.text = "${accountStatementTransaction.product}"
            binding.createdAt.text = "${accountStatementTransaction.createdAt}"
            binding.providername.text = accountStatementTransaction.providerName
            binding.status.text = "${accountStatementTransaction.status}"
            binding.amount.text = accountStatementTransaction.amount.toString()
            binding.balance.text = "${accountStatementTransaction.balance}"
            binding.profit.text = accountStatementTransaction.profit.toString()

            when (accountStatementTransaction.status.lowercase()) {
                "processing", "pending" -> binding.cardView9.setCardBackgroundColor(resources.getColor(
                    R.color.orange))
                "failed" -> binding.cardView9.setCardBackgroundColor(resources.getColor(R.color.red))
                else -> binding.cardView9.setCardBackgroundColor(resources.getColor(R.color.colorGreenLight))
            }

        }

        companion object {
            fun from(parent: ViewGroup): AccountStatementTransactionsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ItemAccountStatementTransactionBinding.inflate(layoutInflater, parent, false)
                return AccountStatementTransactionsViewHolder(binding)
            }
        }
    }
}

class DiffUtilAccountStatementTransactionCallBack :
    DiffUtil.ItemCallback<AccountStatementTransaction>() {
    override fun areItemsTheSame(
        oldItem: AccountStatementTransaction,
        newItem: AccountStatementTransaction,
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: AccountStatementTransaction,
        newItem: AccountStatementTransaction,
    ): Boolean {
        return oldItem == newItem
    }
}