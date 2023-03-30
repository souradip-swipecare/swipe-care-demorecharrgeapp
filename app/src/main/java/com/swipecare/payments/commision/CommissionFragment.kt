package com.swipecare.payments.commision

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.swipecare.payments.adapters.CommissionDataAdapter
import com.swipecare.payments.databinding.FragmentCommissionBinding
import com.swipecare.payments.model.commision.Commission
import com.swipecare.payments.model.commision.CommissionData

class CommissionFragment(private val commission: List<CommissionData>) : Fragment() {

    private lateinit var binding: FragmentCommissionBinding

    companion object {
        fun getInstance(position: Int, commission: Commission): CommissionFragment {
            val commissionAsDictionary = commission.getCommissionDataAsDictionary()
            val keyByIndex = commissionAsDictionary.keys.elementAt(position)
            val valueOfElement = commissionAsDictionary.getValue(keyByIndex)
            return CommissionFragment(valueOfElement ?: emptyList())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentCommissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CommissionDataAdapter()

        binding.commissionRecyclerView.adapter = adapter
        adapter.submitList(commission)
    }
}