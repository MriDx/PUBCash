package com.mridx.pubcash;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.snackbar.Snackbar;
import com.mridx.pubcash.handlers.AppController;
import com.mridx.pubcash.handlers.DataAdapter;
import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.RequestHandler;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class MatchViewActivity extends AppCompatActivity {

    private ArrayList<Integer> mMatchId = new ArrayList<>();
    private ArrayList<String> mName = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();
    private ArrayList<String> mChicken = new ArrayList<>();
    private ArrayList<String> mKill = new ArrayList<>();
    private ArrayList<String> mFees = new ArrayList<>();
    private ArrayList<String> mType = new ArrayList<>();
    private ArrayList<String> mMode = new ArrayList<>();
    private ArrayList<String> mMap = new ArrayList<>();
    private ArrayList<Integer> mTotal = new ArrayList<>();

    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog progressDialog;

    SwipeRefreshLayout mySwipe;

    private Toolbar toolbar;

    private static AppCompatImageView matchViewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.available_matches));
        //getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.logo));
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        mySwipe = findViewById(R.id.mSwipe);
        mySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMatches();
            }
        });


        matchViewImage = (AppCompatImageView) findViewById(R.id.matchViewImage);

        if (AppController.hasNetwork()) {
            loadMatches();
        } else {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Connection Error!");
            alertDialog.setMessage("No Internet Connection");
            alertDialog.setIcon(R.drawable.ic_error);
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                    finish();
                }
            });
            alertDialog.show();
        }

        //CheckStatus();

    }

    private void loadMatches() {

        final String yes = "yes";

        mySwipe.setRefreshing(false);
        mMatchId.clear();
        mName.clear();
        mTime.clear();
        mChicken.clear();
        mKill.clear();
        mFees.clear();
        mType.clear();
        mMode.clear();
        mMap.clear();
        mTotal.clear();

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        class loadMatch extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog("Fetching Match Details");
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("available", yes);
                return requestHandler.sendPostRequest(JavaStrings.URL_GET_MATCH, params);

                // pasing json and add them into arrays

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        if (obj.getString("title").equals("Server offline")) {
                            alertDialog.setTitle(obj.getString("title"));
                            alertDialog.setMessage(obj.getString("message"));
                            alertDialog.setCancelable(false);
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, obj.getString("button"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            alertDialog.show();
                        }else {
                            //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            //alertDialog.setMessage(obj.getString("message"));
                            //alertDialog.setCancelable(true);
                            //alertDialog.show();
                            JSONArray matches = obj.getJSONArray("result");

                            for (int i = 0; i <= matches.length(); i++) {
                                JSONObject match = matches.getJSONObject(i);

                                int matchid = match.getInt("matchid");
                                mMatchId.add(matchid);

                                String name = match.getString("name");
                                mName.add(name);

                                String time = match.getString("time");
                                mTime.add(time);

                                String chicken = match.getString("chicken");
                                mChicken.add(chicken);

                                String kill = match.getString("perkill");
                                mKill.add(kill);

                                String fees = match.getString("fees");
                                mFees.add(fees);

                                String type = match.getString("type");
                                mType.add(type);

                                String mode = match.getString("mode");
                                mMode.add(mode);

                                String map = match.getString("map");
                                mMap.add(map);

                                Integer total = match.getInt("total");
                                mTotal.add(total);

                            }
                        }


/*
                            Matches match = new Matches(
                                    matches.getInt("matchid"),
                                    matches.getString("name"),
                                    matches.getString("time"),
                                    matches.getString("chicken"),
                                    matches.getString("perkill"),
                                    matches.getString("fees"),
                                    matches.getString("type"),
                                    matches.getString("mode"),
                                    matches.getString("map")
                            );



                        /*
                        for (int i = 0; i < 4; i++) {
                            mMatchId.add(matches.getInt("matchid"));
                            mName.add(matches.getString("name"));
                            mTime.add(matches.getString("time"));
                            mChicken.add(matches.getString("chicken"));
                            mKill.add(matches.getString("perkill"));
                            mFees.add(matches.getString("fees"));
                            mType.add(matches.getString("type"));
                            mMode.add(matches.getString("mode"));
                            mMap.add(matches.getString("map"));
                        }
                        */

                    }  else {
                        //Toast.makeText(getApplicationContext(), "Matches not found! Try later.", Toast.LENGTH_SHORT).show();
                        alertDialog.setTitle(obj.getString("title"));
                        alertDialog.setMessage(obj.getString("message"));
                        alertDialog.setIcon(R.drawable.ic_error);
                        alertDialog.setCancelable(true);
                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        });
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerMatchesView);
                final DataAdapter mDataAdapter = new DataAdapter(MatchViewActivity.this, mMatchId, mName, mTime, mChicken, mKill, mFees, mType, mMode, mMap, mTotal );
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mDataAdapter);
                hideDialog();
                Picasso.with(getApplicationContext()).load(JavaStrings.URL_MATCH_VIEW_IMAGE).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(matchViewImage);

            }
        }


/*
        class loadMatch extends AsyncTask<Void, String, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... s) {

                try {
                    JSONObject jObj = new JSONObject();
                    if (!jObj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                        JSONObject matches = jObj.getJSONObject("result");

                        for (int i = 0; i < 4; i++) {
                            mMatchId.add(matches.getInt("matchid"));
                            mName.add(matches.getString("name"));
                            mTime.add(matches.getString("time"));
                            mChicken.add(matches.getString("chicken"));
                            mKill.add(matches.getString("perkill"));
                            mFees.add(matches.getString("fees"));
                            mType.add(matches.getString("type"));
                            mMode.add(matches.getString("mode"));
                            mMap.add(matches.getString("map"));
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.matchesView);
                final DataAdapter mDataAdapter = new DataAdapter(MatchViewActivity.this, mMatchId, mName, mTime, mChicken, mKill, mFees, mType, mMode, mMap );
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mDataAdapter);
            }
        }
        */

        loadMatch lm = new loadMatch();
        lm.execute();






    }
/*
    private void CheckStatus() {



        class checkStatus extends AsyncTask<Void, Void, String> {

            ProgressBar progressDetails;
            AppCompatButton matchJoin;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {

                RequestHandler reqHandler = new RequestHandler();
                HashMap<String, String> values = new HashMap<>();
                values.put("matchid", mID);

                return reqHandler.sendPostRequest(URLs.URL_JOIN_NO, values);
            }

            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);

                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        progressDetails = (ProgressBar) findViewById(R.id.progressDetails);
                        progressDetails.setIndeterminate(false);
                        progressDetails.setMax(100);
                        progressDetails.setProgress(Integer.parseInt(obj.getString("i")));
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }


        }

        checkStatus cs = new checkStatus();
        cs.execute();

    }

*/

    @Override
    protected void onRestart() {
        super.onRestart();
        //loadMatches();
    }

    public void showDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void hideDialog() {
        progressDialog.dismiss();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.from_right, R.anim.to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int itemid = item.getItemId();
        switch (itemid) {
            case android.R.id.home:
                //Snackbar.make(sc, "Home button selected", Snackbar.LENGTH_SHORT).show();
                onBackPressed();
                break;
        }
        return true;
    }
}
