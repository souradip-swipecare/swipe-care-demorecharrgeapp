package com.swipecare.payments.MoneyTransfer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swipecare.payments.CallRestApiForMoneyTransfer;
import com.swipecare.payments.DetectConnection;
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

public class

Add_benificiary_Fragment extends Fragment {
    String mobileValue, nameValue, beneaccountValue , ifscValue, type="";

    AlertDialog alertDialog;
    public static AlertDialog alertDialog_for_bank;

    public static Add_benificiary_Fragment newInstance(String mobile, String name) {
        Add_benificiary_Fragment result = new Add_benificiary_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("mobile", mobile);
        bundle.putString("name", name);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mobileValue = bundle.getString("mobile");
        nameValue = bundle.getString("name");
    }

    public static LinearLayout ll_bank_detail;
    Button button_add_beneficiary;
    EditText beneaccount, benename ;
    ProgressDialog dialog;
    public static EditText beneifsc;

    RelativeLayout rl_bank;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_benificiary_fragment, container, false);
        beneaccount = v.findViewById(R.id.beneaccount);
        benename = v.findViewById(R.id.benename);
        beneifsc = v.findViewById(R.id.ifscCode);

        rl_bank=v.findViewById(R.id.rl_bank);
        rl_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DetectConnection.checkInternetConnection(getActivity())) {
                    mGetBankList();
                } else {
                    Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_add_beneficiary = v.findViewById(R.id.button_add_benificiary);
        ll_bank_detail = v.findViewById(R.id.ll_bank_detail);

        button_add_beneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(getActivity())) {
                    if (benename.equals("")) {
                        Toast.makeText(getContext(), "Enter beneficiary name", Toast.LENGTH_SHORT).show();
                    } else if (beneaccount.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Enter account number..", Toast.LENGTH_SHORT).show();
                    } else if (beneifsc.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Enter beneficiary name", Toast.LENGTH_SHORT).show();
                    } else if (beneifsc.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Please enter ifsc code", Toast.LENGTH_SHORT).show();
                    } else {
                        type="addbeneficiary";
                        beneaccountValue =  beneaccount.getText().toString();
                        new CallMyRestApi().execute(SharePrfeManager.getInstance(getActivity()).mGetBaseUrl()
                            +"api/android/dmt/transaction?apptoken="+SharePrfeManager.getInstance(getActivity()).mGetappToken()
                            +"&user_id="+SharePrfeManager.getInstance(getActivity()).mGetUserId()+"&type="+type
                            +"&mobile="+mobileValue+"&beneifsc="+beneifsc.getText().toString()+"&beneaccount="
                            +beneaccountValue+"&benename="+benename.getText().toString()+"&name="+nameValue);
                    }
                } else {
                    Toast.makeText(getContext(), "No Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    class CallMyRestApi extends CallRestApiForMoneyTransfer
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(getActivity());
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            Log.e("data",s);

            if (type.equalsIgnoreCase("addBeneficiary")) {
                mShowAddBeneStatus(s);
            }
        }
    }

    protected void mShowAddBeneStatus(final String myJSON)
    {
        String status="";
        String message="";
        String myresponsecode="";
        String myrtransid="";

        try {
            JSONObject jsonObject=new JSONObject(myJSON);

            if (jsonObject.has("status")) {
                status = jsonObject.getString("status");
            }

            if (jsonObject.has("message")) {
                message = jsonObject.getString("message");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (status.equalsIgnoreCase("txn")){
            final AlertDialog.Builder builder1= new AlertDialog.Builder(getActivity());
            builder1.setMessage(message);

            builder1.setCancelable(false)
                    .setTitle("Success")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            if (DetectConnection.checkInternetConnection(getActivity())){
                                ((SenderDetailActivity)getActivity()).Update();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            AlertDialog alert = builder1.create();
            alert.show();
        } else{
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    protected void mGetBankList() {
        final ProgressDialog progressDialog;
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching bank list, please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        rl_bank.setClickable(false);

        final List<BankListItems> beneficiaryitems;

        beneficiaryitems=new ArrayList<>();
        final BankListCardAdapter operatorsCardAdapter=new BankListCardAdapter(getActivity(),beneficiaryitems);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, SharePrfeManager.getInstance(getActivity()).mGetBaseUrl()+"api/android/dmt/transaction?apptoken="+SharePrfeManager.getInstance(getActivity()).mGetappToken()+"&user_id="+SharePrfeManager.getInstance(getActivity()).mGetUserId()+"&type=getbank", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();
                rl_bank.setClickable(true);
                Log.e("response", response);
                if (!response.equals(""))
                {
                    String id="";
                    String bank="";
                    String ifsc="";
                    String status="";
                    String message="";

                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        if (jsonObject.has("status")) {
                            status=jsonObject.getString("status");
                        }
                        if (jsonObject.has("message")) {
                            message=jsonObject.getString("message");
                        }

                        if (status.equalsIgnoreCase("txn")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                id = data.getString("id");
                                bank = data.getString("name");
                                ifsc = data.getString("ifsc");

                                BankListItems operators_items = new BankListItems();
                                operators_items.setId(id);
                                operators_items.setBank(bank);
                                operators_items.setIfsc(ifsc);
                                beneficiaryitems.add(operators_items);
                                operatorsCardAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }

                        if (beneficiaryitems!=null) {
                            LayoutInflater inflater2 =(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v2=inflater2.inflate(R.layout.custome_alert_bank_list,null);
                            EditText edittext_search=v2.findViewById(R.id.edittext_search);
                            edittext_search.setVisibility(View.VISIBLE);
                            RecyclerView recyclerview_operator=v2.findViewById(R.id.recyclerview_operator);
                            ImageView imageview_cancel=v2.findViewById(R.id.imageview_cancel);

                            recyclerview_operator.setHasFixedSize(true);
                            recyclerview_operator.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerview_operator.setAdapter(operatorsCardAdapter);

                            edittext_search.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {

                                    if (beneficiaryitems!=null) {
                                        List<BankListItems> temp = new ArrayList();
                                        for (BankListItems d : beneficiaryitems) {
                                            if (d.getBank().toLowerCase().contains(editable.toString().toLowerCase())) {
                                                temp.add(d);
                                            }
                                        }
                                        operatorsCardAdapter.UpdateList(temp);
                                    }
                                }
                            });

                            final AlertDialog.Builder builder2=new AlertDialog.Builder(getActivity());
                            builder2.setCancelable(false);

                            builder2.setView(v2);

                            alertDialog_for_bank=builder2.create();

                            if (!alertDialog_for_bank.isShowing()) {
                                alertDialog_for_bank.show();
                            }
                            imageview_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog_for_bank.dismiss();
                                }
                            });
                        } else
                        {
                            Toast.makeText(getContext(), "Something wrong...", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Something wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                rl_bank.setClickable(true);
                Toast.makeText(getContext(), "Something wrong", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue= Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }
}