package com.swipecare.payments.adapters


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


import com.swipecare.payments.databinding.UserItemBinding
import com.swipecare.payments.model.Userlistitem



class Userlistadapter (val resources: Resources) :
    ListAdapter<Userlistitem, Userlistadapter.Userviewmodel>(
        DiffUtiluserlistTransactionCallBack()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): Userviewmodel {
        return Userviewmodel.from(parent)
    }

    override fun onBindViewHolder(holder: Userviewmodel, position: Int) {
        val accountStatementTransaction = getItem(position)
        holder.bind(resources, accountStatementTransaction)


    }

    class Userviewmodel private constructor(private var binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resources: Resources, accountStatementTransaction: Userlistitem) {
            binding.mainwallet.text = accountStatementTransaction.mainwallet
            binding.retailer.text = accountStatementTransaction.name
            binding.type.text = "${accountStatementTransaction.mobile}"
            binding.type.setOnClickListener {
                val clipboardManager = itemView.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("", accountStatementTransaction.mobile )
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(itemView.context, "Activity copied to clipboard", Toast.LENGTH_SHORT).show()


            }
//            when (accountStatementTransaction.status.lowercase()) {
//                "processing", "pending" -> binding.cardView9.setCardBackgroundColor(resources.getColor(
//                    R.color.orange))
//                "failed" -> binding.cardView9.setCardBackgroundColor(resources.getColor(R.color.red))
//                else -> binding.cardView9.setCardBackgroundColor(resources.getColor(R.color.colorGreenLight))
//            }

        }

        companion object {
            fun from(parent: ViewGroup): Userviewmodel {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    UserItemBinding.inflate(layoutInflater, parent, false)
                return Userviewmodel(binding)
            }
        }

    }

}

class DiffUtiluserlistTransactionCallBack :
    DiffUtil.ItemCallback<Userlistitem>() {
    override fun areItemsTheSame(
        oldItem: Userlistitem,
        newItem: Userlistitem,
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: Userlistitem,
        newItem: Userlistitem,
    ): Boolean {
        return oldItem == newItem
    }
}