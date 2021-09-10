package com.mridx.pubcash.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.mridx.pubcash.R;
import com.mridx.pubcash.WalletActivity;
import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.RequestHandler;
import com.mridx.pubcash.handlers.SharedPrefManager;
import com.mridx.pubcash.handlers.TransAdapter;
import com.mridx.pubcash.handlers.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Transactions extends Fragment {

    private RecyclerView transView;
    private LinearLayoutManager layoutManager;

    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> transid = new ArrayList<>();
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<String> amount = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;

    public static ConstraintLayout parentTrans;

    private AppCompatTextView txtV1;

    private ConstraintLayout transLayout;

    public Transactions() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date.clear();
        transid.clear();
        status.clear();
        amount.clear();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        //return inflater.inflate(R.layout.fragment_transactions, container, false);
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        //final RecyclerView transView = view.findViewById(R.id.transView);
        parentTrans = view.findViewById(R.id.parentTrans);
        transLayout = view.findViewById(R.id.transLayout);
        txtV1 = view.findViewById(R.id.txtV1);
        date.clear();
        transid.clear();
        status.clear();
        amount.clear();
        LoadTrans();
        return view;
    }
    private void LoadTrans() {

        class loadtrans extends AsyncTask<Void, Void, String> {

            final User user = SharedPrefManager.getInstance(getActivity()).getUser();

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap();
                params.put("username", user.getUsername());
                return requestHandler.sendPostRequest(JavaStrings.URL_LOAD_TRANS, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        JSONArray trans = obj.getJSONArray("result");
                        Log.d("transl", String.valueOf(trans.length()));
                        if (trans.length() > 0) {
                            for (int i = 0; i <= trans.length(); i++) {
                                JSONObject m = trans.getJSONObject(i);
                                date.add(m.getString("date"));
                                transid.add(m.getString("paymentid"));
                                status.add(m.getString("status"));
                                amount.add(m.getString("amount"));
                            }

                        } else {
                            transLayout.setVisibility(View.GONE);
                            txtV1.setVisibility(View.VISIBLE);
                            Log.d("errorm", "ERRor MaxxAi");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //final TransAdapter transAdapter = new TransAdapter(date, transid, status, amount);
                TransAdapter transAdapter = new TransAdapter(date, transid, status, amount);
                final RecyclerView transView = Objects.requireNonNull(Transactions.this.getView()).findViewById(R.id.transView);
                layoutManager = new LinearLayoutManager(getActivity());
                transView.setLayoutManager(layoutManager);
                transView.setAdapter(transAdapter);

            }
        }
        loadtrans loadTrans = new loadtrans();
        loadTrans.execute();
    }



}
