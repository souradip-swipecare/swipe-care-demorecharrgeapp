package com.swipecare.payments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swipecare.payments.databinding.ItemRechargeOfferBinding
import com.swipecare.payments.model.Offer
import com.swipecare.payments.model.Plan
import com.swipecare.payments.recharges.MobileRechargePlansActivity


class RechargeOffersAdapter(
    private val onClickListener: OnclickListener,
) : ListAdapter<Offer, RechargeOffersAdapter.RechargeOffersViewHolder>(DiffUtilOfferCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RechargeOffersViewHolder {
        return RechargeOffersViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RechargeOffersViewHolder, position: Int) {
        val quote = getItem(position)
        holder.bind(quote, onClickListener)
    }

    class RechargeOffersViewHolder private constructor(private var binding: ItemRechargeOfferBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(offer: Offer, onClickListener: OnclickListener) {
            binding.planDescription.text = offer.ofrtext
            binding.planAmount.text = offer.price
            binding.planAmount.setOnClickListener {
                onClickListener.onClick(offer)
            }
        }

        companion object {
            fun from(parent: ViewGroup): RechargeOffersViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRechargeOfferBinding.inflate(layoutInflater, parent, false)
                return RechargeOffersViewHolder(binding)
            }
        }
    }

    class OnclickListener(val clickListener: (offer: Offer) -> Unit) {
        fun onClick(offer: Offer) =
            clickListener(offer)
    }
}

class DiffUtilOfferCallBack : DiffUtil.ItemCallback<Offer>() {
    override fun areItemsTheSame(oldItem: Offer, newItem: Offer): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Offer, newItem: Offer): Boolean {
        return oldItem == newItem
    }
}
