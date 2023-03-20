package com.swipecare.payments.Operators;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.swipecare.payments.DetectConnection;
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


/**
 * Created by Basant on 3/29/2018.
 */

public class BottomSheet3DialogFragment extends BottomSheetDialogFragment {

    RecyclerView recyclerview_operator;
    ProgressDialog dialog;

    RecyclerView.LayoutManager layoutManager;
    private List<Operators_Items> beneficiaryitems;

    String type;
    public static String activity_name;
    TextView textview_title;

    EditText edittext_search;


    private BottomSheetBehavior mBehavior;
    ImageView imageview_back_icon;
    public static BottomSheet3DialogFragment dialogFragment;
    String operator;
    OperatorsCardAdapter operatorsCardAdapter=null;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }
        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.bottom_sheet_layout, null);


        dialogFragment=this;
        imageview_back_icon=(ImageView)view.findViewById(R.id.imageview_back_icon);
        imageview_back_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        SharedPreferences sharedPreferences=getContext().getSharedPreferences("alloperators",0);
        operator=sharedPreferences.getString("operator","");

//        Log.e("operator",operator);

        type=getArguments().getString("type");
        activity_name=getArguments().getString("activity");

        view.findViewById(R.id.fakeShadow).setVisibility(View.GONE);
        recyclerview_operator =  view.findViewById(R.id.recyclerview_operator);
        recyclerview_operator.setHasFixedSize(true);
        recyclerview_operator.setLayoutManager(new LinearLayoutManager(getContext()));


        beneficiaryitems=new ArrayList<>();

        operatorsCardAdapter=new OperatorsCardAdapter(getContext(),beneficiaryitems);
        recyclerview_operator.setAdapter(operatorsCardAdapter);

        textview_title=view.findViewById(R.id.textview_title);
        edittext_search=view.findViewById(R.id.edittext_search);

        if (activity_name.equalsIgnoreCase("electricity")) {
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

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        dialog.getActionBar();

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
                        //or use .equal(text) with you want equal match
                        //use .toLowerCase() for better matches
                        if (d.getOperator_name().toLowerCase().contains(s.toString().toLowerCase())) {
                            temp.add(d);
                        }
                    }
                    //update recyclerview
                    operatorsCardAdapter.UpdateList(temp);
                }

            }
        });


        return dialog;

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

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

                try {
                    URL url = new URL(SharePrfeManager.getInstance(getActivity()).mGetBaseUrl()+"api/android/recharge/providers?type="+type);
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

                //Do something with the JSON string
                textview_title.setText("Select Provider");
//                Log.e("operator response",result);

                if (!result.equals(""))
                {
                    try
                    {
                        JSONObject jsonObject=new JSONObject(result);
                        if (jsonObject.has("statuscode"))
                        {
                            if (jsonObject.getString("statuscode").equalsIgnoreCase("TXN"))
                            {
                                JSONArray jsonArray=jsonObject.getJSONArray("data");
                                for (int i=0; i<jsonArray.length(); i++)
                                {
                                    JSONObject data=jsonArray.getJSONObject(i);

                                    Operators_Items operators_items = new Operators_Items();
                                    operators_items.setOperator_id(data.getString("id"));
                                    operators_items.setOperator_name(data.getString("name"));
                                    beneficiaryitems.add(operators_items);
                                    operatorsCardAdapter.notifyDataSetChanged();
                                }

                            }
                            else
                            {
                                if (jsonObject.has("message"))
                                {
                                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                                dialogFragment.dismiss();
                            }
                        }
                        else
                        {
                            Toast.makeText(getActivity(), " went wrong", Toast.LENGTH_SHORT).show();
                            dialogFragment.dismiss();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    dialogFragment.dismiss();
                }
            }
        }

        getJSONData getJSONData=new getJSONData();
        getJSONData.execute();
    }


}