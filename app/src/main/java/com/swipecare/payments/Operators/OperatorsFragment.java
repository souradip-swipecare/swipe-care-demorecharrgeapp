package com.swipecare.payments.Operators;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.swipecare.payments.DetectConnection;
import com.swipecare.payments.MoneyTransfer.BankListCardAdapter;
import com.swipecare.payments.MoneyTransfer.BankListItems;
import com.swipecare.payments.R;
import com.swipecare.payments.SharePrfeManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OperatorsFragment extends Fragment implements DismissFragment {


    RecyclerView recyclerview_operator;
    ProgressDialog dialog;

    RecyclerView.LayoutManager layoutManager;
    private List<Operators_Items> beneficiaryitems;
    private List<BankListItems> bankListItemsList;

    String type;
    public static String activity_name;
    TextView textview_title;
    EditText edittext_search;
    OperatorsCardAdapter operatorsCardAdapter=null;
    BankListCardAdapter bankCard = null;

    ImageView imageview_back_icon;
    String operator;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.bottom_sheet_layout,container,false);


        type=getArguments().getString("type");
        activity_name=getArguments().getString("activity");


        imageview_back_icon=(ImageView)view.findViewById(R.id.imageview_back_icon);
        imageview_back_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        recyclerview_operator =  view.findViewById(R.id.recyclerview_operator);
        recyclerview_operator.setHasFixedSize(true);
        recyclerview_operator.setLayoutManager(new LinearLayoutManager(getContext()));


        beneficiaryitems=new ArrayList<>();
        bankListItemsList=new ArrayList<>();

        operatorsCardAdapter=new OperatorsCardAdapter(getContext(),beneficiaryitems);
        recyclerview_operator.setAdapter(operatorsCardAdapter);

        bankCard=new BankListCardAdapter(getContext(),bankListItemsList);
        recyclerview_operator.setAdapter(operatorsCardAdapter);

        textview_title=view.findViewById(R.id.textview_title);
        edittext_search=view.findViewById(R.id.edittext_search);

        if (activity_name.equalsIgnoreCase("electricity")) {
            edittext_search.setVisibility(View.VISIBLE);
        }

        if (activity_name.equalsIgnoreCase("bank")) {
            edittext_search.setVisibility(View.VISIBLE);
        }

        if (DetectConnection.checkInternetConnection(getActivity()))
        {
            mGetLoginData(activity_name);
        }
        else
        {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

        edittext_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (beneficiaryitems!=null) {
                    List<Operators_Items> temp = new ArrayList();
                    for (Operators_Items d : beneficiaryitems) {
                        if (d.getOperator_name().toLowerCase().contains(s.toString().toLowerCase())) {
                            temp.add(d);
                        }
                    }
                    operatorsCardAdapter.UpdateList(temp);
                }
            }
        });
        return view;
    }

    private  void  mGetLoginData(final  String type) {
        class getJSONData extends AsyncTask<String, String, String> {

            HttpURLConnection urlConnection;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                textview_title.setText("Please wait...");
            }
            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();
                String myurl;
                try {
                    if(type == "bank"){
                        myurl = SharePrfeManager.getInstance(getActivity()).mGetBaseUrl()+"api/android/dmt/transaction?apptoken="+SharePrfeManager.getInstance(getActivity()).mGetappToken()+"&user_id="+SharePrfeManager.getInstance(getActivity()).mGetUserId()+"&type=getbank";
                    }else{
                        myurl = SharePrfeManager.getInstance(getActivity()).mGetBaseUrl()+"api/android/recharge/providers?type="+type;
                    }
//                    Log.e("url", myurl);
                    URL url = new URL(myurl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
                return result.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                if (type == "bank") {
                    textview_title.setText("Select Bank");
                }else{
                    textview_title.setText("Select Provider");
                }

                if (!result.equals("")) {
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        if (jsonObject.has("statuscode")) {
                            if (jsonObject.getString("statuscode").equalsIgnoreCase("TXN")) {
                                JSONArray jsonArray=jsonObject.getJSONArray("data");
                                for (int i=0; i<jsonArray.length(); i++)
                                {
                                    JSONObject data=jsonArray.getJSONObject(i);

                                    if (type != "bank") {
                                        Operators_Items operators_items = new Operators_Items();
                                        operators_items.setOperator_id(data.getString("id"));
                                        operators_items.setOperator_name(data.getString("name"));

                                        beneficiaryitems.add(operators_items);
                                        operatorsCardAdapter.notifyDataSetChanged();
                                    }else{
                                        BankListItems bankitem = new BankListItems();
                                        bankitem.setId(data.getString("id"));
                                        bankitem.setBank(data.getString("name"));
                                        bankitem.setIfsc(data.getString("ifsc"));

                                        bankListItemsList.add(bankitem);
                                        operatorsCardAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                if (jsonObject.has("message")) {
                                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                                getFragmentManager().popBackStack();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            getFragmentManager().popBackStack();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                }
            }
        }

        getJSONData getJSONData=new getJSONData();
        getJSONData.execute();
    }

    public void DissMIss()
    {

    }

}
