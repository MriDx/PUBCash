package com.mridx.pubcash;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.MenuItem;

import com.mridx.pubcash.handlers.CompleteResultAdapter;
import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.RequestHandler;
import com.mridx.pubcash.handlers.ResultAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ResultActivity extends AppCompatActivity {

    ArrayList<Integer> sl = new ArrayList<>();
    ArrayList<String> k_no = new ArrayList<>();
    ArrayList<String> ingamename = new ArrayList<>();
    ArrayList<String> won = new ArrayList<>();

    ArrayList<Integer> sl1 = new ArrayList<>();
    ArrayList<String> k_no1 = new ArrayList<>();
    ArrayList<String> ingamename1 = new ArrayList<>();
    ArrayList<String> won1 = new ArrayList<>();

    private LinearLayoutManager mLayoutManager;

    private ProgressDialog progressDialog;

    private Toolbar toolbar;
    private AppCompatTextView rMID, rMTime, rPrize, rKill, rFees;
    public static String perkill;
    public static String prize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        final Bundle bundle = getIntent().getExtras();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rMID = findViewById(R.id.rMID);
        rMID.setText(bundle.getString("resultsMID"));

        rMTime = findViewById(R.id.rMTime);
        rMTime.setText(bundle.getString("resultsMTime"));

        rPrize = findViewById(R.id.rPrize);
        rPrize.setText(bundle.getString("resultsMPrize"));
        prize = bundle.getString("resultsMPrize");

        rKill = findViewById(R.id.rKill);
        rKill.setText(bundle.getString("resultsMKill"));
        perkill = bundle.getString("resultsMKill");

        rFees = findViewById(R.id.rFees);
        rFees.setText(bundle.getString("resultsMFees"));

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        LoadWinner();

    }

    private void LoadWinner() {

        showDialog("Retrieving Results...");


        class loadWinner extends AsyncTask<Void, Void, String> {

            final Bundle bundle = getIntent().getExtras();

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("matchid", bundle.getString("resultsMID"));
                params.put("result", "yes");
                params.put("winner", "winner");
                return requestHandler.sendPostRequest(JavaStrings.URL_LOAD_WINNERS, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        JSONArray winners = obj.getJSONArray("win");
                        for (int i = 0; i <= winners.length(); i++) {
                            JSONObject winner = winners.getJSONObject(i);
                            sl.add(winner.getInt("sl"));
                            k_no.add(winner.getString("k_no"));
                            ingamename.add(winner.getString("ingamename"));
                            won.add(winner.getString("won"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                RecyclerView chickenHolder = findViewById(R.id.wwcd);
                ResultAdapter resultAdapter = new ResultAdapter(sl, k_no, ingamename, won);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                chickenHolder.setLayoutManager(mLayoutManager);
                chickenHolder.setAdapter(resultAdapter);


                JSONObject obj = null;
                try {
                    obj = new JSONObject(s);
                    JSONArray complete = obj.getJSONArray("results");
                    for (int i = 0; i <= complete.length(); i++) {
                        JSONObject winner = complete.getJSONObject(i);
                        sl1.add(winner.getInt("sl"));
                        k_no1.add(winner.getString("k_no"));
                        ingamename1.add(winner.getString("ingamename"));
                        won1.add(winner.getString("won"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RecyclerView completeResultHolder = findViewById(R.id.crRecycler);
                CompleteResultAdapter completeResultAdapter = new CompleteResultAdapter(sl1, k_no1, ingamename1, won1);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                completeResultHolder.setLayoutManager(mLayoutManager);
                completeResultHolder.setAdapter(completeResultAdapter);

                hideDialog();
            }
        }

        loadWinner lw = new loadWinner();
        lw.execute();

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
