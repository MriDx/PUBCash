package com.mridx.pubcash;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mridx.pubcash.handlers.AppController;
import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.ParticipantsAdapter;
import com.mridx.pubcash.handlers.ParticipantsAdapter1;
import com.mridx.pubcash.handlers.RequestHandler;
import com.mridx.pubcash.handlers.SharedPrefManager;
import com.mridx.pubcash.handlers.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.mridx.pubcash.DashboardActivity.BALANCE;
import static com.mridx.pubcash.DashboardActivity.COMPLETE;
import static com.mridx.pubcash.DashboardActivity.COMPLETEPROFILE;

public class MatchDetailsActivity1 extends AppCompatActivity {


    private AppCompatTextView matchNoDetails, battleNameDetails, timeViewDetails, prizeViewDetails, killViewDetails, feesViewDetails, typeViewDetails, modeViewDetails, mapViewDetails, tandc;
    //private String matchNo, name, time, chicken, kill, fees, type, mode, map;
    private AppCompatButton joinMatch;
    SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private CoordinatorLayout matchDetailsParent;
    private LinearLayout participantsLayout;
    private AppCompatButton loadParticipants;

    public int totalJoined;

    private ImageButton reload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchdetails);
        final Bundle details = getIntent().getExtras();


        this.progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.match_details);

        if (!AppController.hasNetwork()) {
            finish();
        }

        matchNoDetails = findViewById(R.id.matchNoDetails);
        matchNoDetails.setText(details.getString("matchid"));

        battleNameDetails = findViewById(R.id.battleNameDetails);
        battleNameDetails.setText("Match Name : " + details.getString("name"));

        timeViewDetails = findViewById(R.id.timeViewDetails);
        timeViewDetails.setText("Match Time and Date : " + details.getString("time"));

        prizeViewDetails = findViewById(R.id.prizeViewDetails);
        prizeViewDetails.setText(details.getString("chicken"));

        killViewDetails = findViewById(R.id.killViewDetails);
        killViewDetails.setText(details.getString("kill"));

        feesViewDetails = findViewById(R.id.feesViewDetails);
        feesViewDetails.setText(details.getString("fees"));

        typeViewDetails = findViewById(R.id.typeViewDetails);
        typeViewDetails.setText(details.getString("type"));

        modeViewDetails = findViewById(R.id.modeViewDetails);
        modeViewDetails.setText(details.getString("mode"));

        mapViewDetails = findViewById(R.id.mapViewDetails);
        mapViewDetails.setText(details.getString("map"));

        matchDetailsParent = findViewById(R.id.matchDetailsParent);

        reload = findViewById(R.id.reload);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshMatchDetails);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CheckStatus();
            }
        });

        participantsLayout = findViewById(R.id.participantsLayout);
        loadParticipants = findViewById(R.id.loadParticipants);
        loadParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Participants();
                loadParticipants.setVisibility(View.GONE);
                reload.setVisibility(View.VISIBLE);
            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Participants();
            }
        });

        joinMatch = (AppCompatButton) findViewById(R.id.matchJoin);
        //startActivity(new Intent(getApplicationContext(), JoinMatch.class));

        joinMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (COMPLETEPROFILE) {
                    if (joinMatch.getText().equals("JOIN")) {
                        final int fees = Integer.parseInt(details.getString("fees"));
                        showDialog("Checking Wallet Balance...");
                        if (fees > BALANCE) {
                            //Toast.makeText(getApplicationContext(), "Payment page", Toast.LENGTH_SHORT).show();

                            final AlertDialog alertDialog = new AlertDialog.Builder(MatchDetailsActivity1.this).create();
                            alertDialog.setTitle("Load money to wallet!");
                            alertDialog.setMessage("Your wallet balance is lower that the fees for this match. \nLoad money to wallet to continue !");
                            alertDialog.setCancelable(false);
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Load Now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), WalletActivity.class);
                                    //intent.putExtra("matchid", details.getString("matchid"));
                                    //intent.putExtra("amount", details.getString("fees"));
                                    startActivity(intent);
                                }
                            });
                            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();

                                }
                            });
                            alertDialog.show();
                            hideDialog();
                        } else if (fees <= BALANCE) {
                            //deduct balance
                            hideDialog();
                            final AlertDialog alertDialog = new AlertDialog.Builder(MatchDetailsActivity1.this).create();
                            alertDialog.setTitle("Payment Confirmation!");
                            alertDialog.setMessage("Sure to join for this Match ? \nThis will cost you Rs. " + fees);
                            alertDialog.setCancelable(false);
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deductBalance(fees, BALANCE);
                                }
                            });
                            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                }
                            });
                            alertDialog.show();
                        }
                    } else if (joinMatch.getText().equals("Match Full")) {
                        Snackbar.make(matchDetailsParent, "Match Full", Snackbar.LENGTH_LONG).show();
                    }
                    else {
                        MatchJoin(); //Starts the joining process
                    }

                } else {
                    Snackbar.make(matchDetailsParent, "Complete your profile to join in match", Snackbar.LENGTH_LONG).show();
                }

            }
        });

        if (AppController.hasNetwork()) {
            CheckStatus();
        } else {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setIcon(R.drawable.ic_error);
            alertDialog.setTitle("Connection Error");
            alertDialog.setMessage("No Internet Connection");
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


        tandc = (AppCompatTextView) findViewById(R.id.tandc);
        //tandc.setText("1. This is first line. \n2. This is second line.");

        //CheckRoom();


    }


    private void MatchJoin() {

        //starts joining process here

        final User user = SharedPrefManager.getInstance(this).getUser();
        final String token = user.getToken();
        final String matchid = matchNoDetails.getText().toString();
        final String ingamename = user.getIngamename();
        final String status = "joined";

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();


        class matchJoin extends AsyncTask<Void, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                params.put("matchid", matchid);
                params.put("token", token);
                params.put("ingamename", ingamename);
                params.put("status", status);
                return requestHandler.sendPostRequest(JavaStrings.URL_JOIN_MATCH, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jObj = new JSONObject(s);
                    if (!jObj.getBoolean("error")) {
                        //Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();
                        //alertDialog.setMessage(jObj.getString("message"));
                        //alertDialog.setCancelable(true);
                        //alertDialog.show(); // joined for this match
                        CheckStatus(); // check how many joined
                    } else {
                        hideDialog();
                        Snackbar.make(matchDetailsParent, "Match Full", Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        if (joinMatch.getText().equals("Get Room ID")) {
            //startActivity(new Intent(MatchDetails.this, RoomView.class));
            CheckRoom();
        } else {
            matchJoin mj = new matchJoin();
            mj.execute();
        }
    }


    private void CheckStatus() {

        swipeRefreshLayout.setRefreshing(false);
        Bundle m = getIntent().getExtras();
        final String mID = m.getString("matchid");

        showDialog("Checking status...");

        class checkStatus extends AsyncTask<Void, Void, String> {

            ContentLoadingProgressBar progressDetails;
            AppCompatButton matchJoin;
            AppCompatTextView joinedUsers;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {

                RequestHandler reqHandler = new RequestHandler();
                HashMap<String, String> values = new HashMap<>();
                values.put("matchid", mID);

                return reqHandler.sendPostRequest(JavaStrings.URL_JOIN_NO, values);
            }

            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);

                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        progressDetails = (ContentLoadingProgressBar) findViewById(R.id.progressDetails);
                        progressDetails.setIndeterminate(false);
                        progressDetails.setMax(100);
                        progressDetails.setProgress(Integer.parseInt(obj.getString("i")));

                        totalJoined = Integer.parseInt(obj.getString("i"));

                        joinedUsers  = (AppCompatTextView) findViewById(R.id.joinedUsers);
                        joinedUsers.setText(obj.getString("i") + " /100 Joined");

                        tandc.setText(obj.getString("tnc"));
                        CheckJoined(); // this is check if the user joined or not
                        //Participants();

                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }


        }

        checkStatus cs = new checkStatus();
        cs.execute();

    }




    private void CheckJoined() {

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        User user = SharedPrefManager.getInstance(this).getUser();

        Bundle m1 = getIntent().getExtras();
        final String mID1 = Objects.requireNonNull(m1).getString("matchid");
        final String tk = user.getToken();


        class checkJoined extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler rHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("matchid", mID1);
                params.put("token", tk);
                return rHandler.sendPostRequest(JavaStrings.URL_CHECKED_JOINED, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject obj = new JSONObject(s);
                    if (totalJoined < 100) {
                        if (!obj.getBoolean("error")) {
                            joinMatch.setText("Get Room ID");
                            hideDialog();
                        } else {
                            joinMatch.setText(obj.getString("message"));
                            //joinMatch.setEnabled(false);
                            hideDialog();
                        }
                    } else {
                        if (!obj.getBoolean("error")) {
                            joinMatch.setText("Get Room ID");
                            hideDialog();
                        } else {
                            //joinMatch.setEnabled(false);
                            joinMatch.setText("Match Full");
                            hideDialog();
                        }
                        //joinMatch.setText("Match Full");
                        hideDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Participants();

            }
        }

        checkJoined cJoined = new checkJoined();
        cJoined.execute();

    }

    private void Participants() {
        final ArrayList<String> partiCi = new ArrayList<>();
        final ArrayList<Integer> slno = new ArrayList<>();
        //final TextView participantsHead;
        participantsLayout.setVisibility(View.VISIBLE);
        Bundle m1 = getIntent().getExtras();
        final String mID1 = m1.getString("matchid");
        showDialog("Loading...");

        class participants extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("matchid", mID1 );
                return requestHandler.sendPostRequest(JavaStrings.URL_GET_PARTICIPANTS, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        JSONArray participants = obj.getJSONArray("result");
                        if (participants.length() > 0) {
                            TextView participantsHead = (TextView) findViewById(R.id.participantHead);
                            participantsHead.setText("Participants : ");
                            for (int i = 0; i <= participants.length(); i++) {
                                JSONObject participant = participants.getJSONObject(i);
                                String username = participant.getString("ingamename");
                                partiCi.add(username);
                                int slNo = Integer.parseInt(participant.getString("sl"));
                                slno.add(slNo);

                            }

                        } else {
                            TextView participantsHead = (TextView) findViewById(R.id.participantHead);
                            participantsHead.setText("Nobody has joined yet");

                        }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final RecyclerView participantsView = (RecyclerView) findViewById(R.id.participantsView);
                final ParticipantsAdapter1 participantsAdapter = new ParticipantsAdapter1(MatchDetailsActivity1.this, partiCi, slno);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                participantsView.setLayoutManager(layoutManager);
                participantsView.setAdapter(participantsAdapter);

                if (progressDialog.isShowing()) {
                    hideDialog();
                }
                // CheckRoom();
            }
        }

        participants part = new participants();
        part.execute();

    }


    private void CheckRoom() {

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        showDialog("Checking Room...");
        class checkRoom extends AsyncTask<Void, Void, String> {

            User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

            Bundle details = getIntent().getExtras();

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler reqHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("matchid", details.getString("matchid"));
                params.put("token", user.getToken());
                return reqHandler.sendPostRequest(JavaStrings.URL_GET_ROOM, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        JSONObject res = obj.getJSONObject("result");
                        alertDialog.setTitle("Room ID and Password ");
                        alertDialog.setMessage("Room ID : " + res.getString("roomid") + "\nRoom Pass : " + res.getString("roompass")
                                + "\nYour Slot no. is : " + res.getString("sl") + "\n*Join only on you slot.");
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Launch PUBGM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Open PUBG", Toast.LENGTH_SHORT).show();
                            }
                        });
                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                        //joinMatch.setText("Room ID");
                        hideDialog();
                    } else {
                        //joinMatch.setText("Room ID not available");
                        alertDialog.setTitle("Room ID and Password");
                        alertDialog.setMessage("Room ID and Password is not generated yet for this Match");
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.hide();
                            }
                        });
                        alertDialog.show();
                        hideDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //String status = joinMatch.getText().toString();
        //if (status.equals("Get Room ID")) {
        checkRoom checkroom = new checkRoom();
        checkroom.execute();
        // }

    }


    private void deductBalance(final int fees, final int BALANCE) {
        if (progressDialog.isShowing()) {
            hideDialog();
            showDialog("Registering you for the match...");
        }
        showDialog("Registering you for the match...");
        @SuppressLint("StaticFieldLeak")
        class deductbal extends AsyncTask<Void, Void, String> {
            private int left = BALANCE - fees;
            private User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler req = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                params.put("token", user.getToken());
                params.put("bal", String.valueOf(left));
                return req.sendPostRequest(JavaStrings.URL_UPDATE_BAL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        MatchJoin(); //Record joining details
                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DashboardActivity.BALANCE = left; // this will update balance locally

                Log.d("MY", String.valueOf(BALANCE));
            }
        }
        deductbal deduct = new deductbal();
        deduct.execute();
    }


    private void showDialog(String message) {
        this.progressDialog.setMessage(message);
        this.progressDialog.show();
    }

    public void hideDialog() {
        this.progressDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int itemid = item.getItemId();
        switch (itemid) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.from_right, R.anim.to_left);
    }


}
