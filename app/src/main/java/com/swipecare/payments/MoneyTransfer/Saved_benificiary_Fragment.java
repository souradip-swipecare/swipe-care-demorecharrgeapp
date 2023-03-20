package com.swipecare.payments.MoneyTransfer;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.swipecare.payments.CallRestApiForMoneyTransfer;
import com.swipecare.payments.R;
import com.swipecare.payments.SharePrfeManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Basant on 6/8/2017.
 */

public class Saved_benificiary_Fragment extends Fragment {

    public static Saved_benificiary_Fragment newInstance(String s, String name,String json) {
        Saved_benificiary_Fragment result = new Saved_benificiary_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("sender_number", s);
        bundle.putString("name", name);
        bundle.putString("json", json);
        result.setArguments(bundle);
        return result;
    }

    String sender_number,name="",json="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=this.getArguments();
        sender_number=bundle.getString("sender_number");
        name=bundle.getString("name");
        json=bundle.getString("json");
    }


    LinearLayout ll_contain_listview;
    LinearLayout ll_contain_norecepient_found;

    RecyclerView recyclerview_benificiary;
    RecyclerView.LayoutManager layoutManager;
    private List<Beneficiary_Items> beneficiaryitems;
    Beneficiry_CardAdapter beneficiry_cardAdapter = null;

    String message,status="";

    String username,password;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.saved_benificiary_fragment, container, false);

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("user",0);
        username=sharedPreferences.getString("username","");
        password=sharedPreferences.getString("password","");
        beneficiaryitems = new ArrayList<>();

        recyclerview_benificiary = v.findViewById(R.id.recyclerview_plan);
        recyclerview_benificiary.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerview_benificiary.setLayoutManager(layoutManager);


        beneficiry_cardAdapter = new Beneficiry_CardAdapter(getActivity(), beneficiaryitems);
        recyclerview_benificiary.setAdapter(beneficiry_cardAdapter);

        ll_contain_listview = v.findViewById(R.id.ll_contain_listview);
        ll_contain_norecepient_found = v.findViewById(R.id.ll_contain_norecepient_found);

        new CallMyRestApi().execute(SharePrfeManager.getInstance(getActivity()).mGetBaseUrl()+"api/android/dmt/transaction?apptoken="+SharePrfeManager.getInstance(getActivity()).mGetappToken()+"&user_id="+SharePrfeManager.getInstance(getActivity()).mGetUserId()+"&type=verification&mobile="+sender_number);
        return v;
    }

    protected void mShowBeneficieryList(final String json)
    {
        String status="";
        String message="";
        try
        {
            JSONObject jsonObject=new JSONObject(json);
            if (jsonObject.has("status"))
            {
                status=jsonObject.getString("status");
            }
            if (jsonObject.has("message"))
            {
                message=jsonObject.getString("message");
            }

            if (status.equalsIgnoreCase("txn"))
            {
                JSONArray jsonArray=jsonObject.getJSONArray("beneficiary");
                for (int i=0; i<jsonArray.length(); i++)
                {
                    JSONObject data=jsonArray.getJSONObject(i);
                    Beneficiary_Items item=new Beneficiary_Items();
                    item.setBeneid(data.getString("beneid"));
                    item.setBenename(data.getString("benename"));
                    item.setBeneaccount(data.getString("beneaccount"));
                    item.setBenebank(data.getString("benebank"));
                    item.setBeneifsc(data.getString("beneifsc"));
                    item.setSendernumber(sender_number);
                    item.setSendername(name);
                    beneficiaryitems.add(item);
                    beneficiry_cardAdapter.notifyDataSetChanged();
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    class CallMyRestApi extends CallRestApiForMoneyTransfer
    {

        ProgressDialog dialog;
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            mShowBeneficieryList(s);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(getActivity());
            dialog.setMessage("Please wait...");
            dialog.show();
            dialog.setCancelable(false);

        }
    }
}