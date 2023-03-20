package com.swipecare.payments.recharges

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.swipecare.payments.adapters.RechargePlansListAdapter
import com.swipecare.payments.databinding.FragmentRechargePlansBinding
import com.swipecare.payments.model.Plan


class RechargePlansFragment : Fragment() {

    private lateinit var binding: FragmentRechargePlansBinding
    private lateinit var rechargePlansActivity: MobileRechargePlansActivity

    companion object {
        fun getInstance(): RechargePlansFragment {
            return RechargePlansFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentRechargePlansBinding.inflate(inflater, container, false)
//            DataBindingUtil.setContentView(requireActivity(), R.layout.fragment_recharge_plans)
//        coroutineScope = CoroutineScope(Dispatchers.IO)
/*        Toast.makeText(requireContext(),
            "${arguments?.getParcelableArrayList<Plan>("plans")}",
            Toast.LENGTH_SHORT).show()*/
        /*    val adapter = RechargePlansListAdapter(RechargePlansListAdapter.OnclickListener {
    //            val intent = Intent()
    //            intent.putExtra("rechargeAmount",it.price)
            })
            binding.plansRecyclerView.adapter = adapter
            Log.i("Hari",
                "onCreateView: hell0 hari ${arguments?.getParcelableArrayList<Plan>("plans")}")
            adapter.submitList(arguments?.getParcelableArrayList<Plan>("plans"))
            Log.i("Hari", "onCreateView: adapter added")*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = arguments?.getParcelableArrayList<Plan>("plans")
        val adapter =
            RechargePlansListAdapter(RechargePlansListAdapter.OnclickListener { plan, activity ->
                val intent = Intent()
                intent.putExtra("planPrice", plan.price.toString())
                activity.setResult(Activity.RESULT_OK, intent)
//            Toast.makeText(requireContext(), "clicked me! ${plan.price.toString()}", Toast.LENGTH_SHORT).show()
                activity.finish()
            }, rechargePlansActivity)

        binding.plansRecyclerView.adapter = adapter
        adapter.submitList(data?.toList())
    }

    fun setActivity(activity: MobileRechargePlansActivity) {
        rechargePlansActivity = activity
    }
}