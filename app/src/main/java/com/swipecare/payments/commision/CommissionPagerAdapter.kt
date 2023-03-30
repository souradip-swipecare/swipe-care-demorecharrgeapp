package com.swipecare.payments.commision

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.swipecare.payments.model.commision.Commission


class CommissionPagerAdapter(
    activity: AppCompatActivity,
    private val itemsCount: Int,
    private val commission: Commission,
) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment =
        CommissionFragment.getInstance(position, commission)
}