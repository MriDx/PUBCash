package com.mridx.pubcash;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.RequestHandler;
import com.mridx.pubcash.handlers.ResultsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ResultsActivity extends AppCompatActivity {

    ArrayList<Integer> matchid = new ArrayList<>();
    ArrayList<String> matchname = new ArrayList<>();
    ArrayList<String> matchtime = new ArrayList<>();
    ArrayList<String> mChicken = new ArrayList<>();
    ArrayList<String> mKill = new ArrayList<>();
    ArrayList<String> mFees = new ArrayList<>();

    private RecyclerView.LayoutManager mLayoutManager;

    private Toolbar toolbar;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        LoadResults();
    }

    private void LoadResults() {

        showDialog("Loading ...");

        class loadResults extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("result", "yes");
                return requestHandler.sendPostRequest(JavaStrings.URL_LOAD_RESULTS, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        JSONArray results = obj.getJSONArray("result");
                        for (int i = 0; i <= results.length(); i++) {
                            JSONObject result = results.getJSONObject(i);
                            matchid.add(result.getInt("matchid"));
                            matchname.add(result.getString("name"));
                            matchtime.add(result.getString("time"));
                            mChicken.add(result.getString("chicken"));
                            mKill.add(result.getString("perkill"));
                            mFees.add(result.getString("fees"));
                        }
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(ResultsActivity.this).create();
                        alertDialog.setTitle("No Results Available");
                        alertDialog.setMessage("There is no results available right now.");
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        });
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RecyclerView resultsHolder = findViewById(R.id.resultsHolder);
                ResultsAdapter resultsAdapter = new ResultsAdapter(matchid, matchname, matchtime, mChicken, mKill, mFees);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                resultsHolder.setLayoutManager(mLayoutManager);
                resultsHolder.setAdapter(resultsAdapter);
                hideDialog();
            }
        }

        loadResults loadres = new loadResults();
        loadres.execute();

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
    }
}
