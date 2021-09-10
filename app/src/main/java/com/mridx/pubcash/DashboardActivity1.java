package com.mridx.pubcash;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.mridx.pubcash.handlers.AppController;
import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.RequestHandler;
import com.mridx.pubcash.handlers.SharedPrefManager;
import com.mridx.pubcash.handlers.SwipeTouchTracer;
import com.mridx.pubcash.handlers.User;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private User user;
    public static String HELP;
    public static String NOTICE;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private AppCompatTextView noticeBody;
    private NestedScrollView sc;
    private AppCompatTextView nameView, balView, mPlayed, mKill, mWin;
    private CircleImageView profilePicView;
    private String username, token, ingamename, dob;
    public boolean PROFILE;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        showDialog("Loading...");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user = SharedPrefManager.getInstance(this).getUser();
        username = user.getUsername();
        token = user.getToken();
        ingamename = user.getIngamename();
        dob = user.getDob();


        noticeBody = findViewById(R.id.noticeBody);

        nameView = findViewById(R.id.nameView);
        balView = findViewById(R.id.balView);
        profilePicView = findViewById(R.id.dashboardProfileImage);
        mPlayed = findViewById(R.id.mPlayed);
        mKill = findViewById(R.id.mKill);
        mWin = findViewById(R.id.mWin);

        sc = findViewById(R.id.sc);
        sc.setOnTouchListener(new SwipeTouchTracer(this){
            @Override
            public void onSwipeLeft() {
                //Snackbar.make(sc, "Left Swipe", Snackbar.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity1.this, WalletActivity.class));
                overridePendingTransition(R.anim.from_right, R.anim.to_left);
            }
            @Override
            public void onSwipeRight() {
                //Snackbar.make(sc, "Right Swipe", Snackbar.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity1.this, MatchViewActivity.class));
                overridePendingTransition(R.anim.from_left, R.anim.to_right);
            }
        });

        //LoadNotice();

        // Load User's name and images
        nameView.setText(user.getFullname());

        if (!AppController.hasNetwork()) {
            Snackbar.make(sc, "No Internet Connection", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRestart();
                }
            }).show();
        } else if (AppController.hasNetwork()) {
            String gender = user.getGender();
            Picasso.with(DashboardActivity1.this)
                    .load(JavaStrings.FIREBASE_ROOT + gender +".png" + JavaStrings.FIREBASE_END)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(profilePicView);

            //Load user's information
            LoadAll();

            if (NOTICE.isEmpty()) {
                LoadNotice();
            }

            if (HELP.isEmpty()) {
                //GetHelpLink();
            }

        }

    }


    private void LoadAll() {

        class loadAll extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("token", token);
                return requestHandler.sendPostRequest(JavaStrings.KARA, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    final JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        JSONObject prop = obj.getJSONObject("prop");
                        balView.setText(prop.getString("balance"));
                        mKill.setText(prop.getString("t_kill"));
                        mWin.setText(prop.getString("t_won"));
                        mPlayed.setText(prop.getString("t_join"));
                    } else {
                        final String message = obj.getString("message");
                        Snackbar.make(sc, message, Snackbar.LENGTH_LONG)
                                .setAction("Report", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Call Report
                                        Report(message);
                                    }
                                })
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        loadAll loadAll = new loadAll();
        loadAll.execute();
    }


    private void LoadNotice() {
        class loadNotice extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "new");
                return requestHandler.sendPostRequest(JavaStrings.DPA, params);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        noticeBody.setText(obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        loadNotice loadNotice = new loadNotice();
        loadNotice.execute();
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (ingamename.isEmpty() || dob.isEmpty()) {
            PROFILE = false;
            showAlert();
        }

    }

    private void showAlert() {
            Snackbar.make(sc, "Profile is incomplete. Please update your profile", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Know More", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(DashboardActivity1.this).create();
                            alertDialog.setTitle("Your Profile is Incomplete!");
                            alertDialog.setMessage("Your profile is incomplete. Please provide us more information about you." +
                                    "We want your Date of Birth , Phone no. and PUBG Mobile username and id.");
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Update Now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(DashboardActivity1.this, EditProfileActivity.class));
                                }
                            });
                            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Update Later", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                    showAlert();
                                }
                            });
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                            //showBtmShtDlg();
                        }
                    })
                    .show();
            Snackbar.make(sc, "Profile is incomplete. Please update your profile", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Know More", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(DashboardActivity1.this).create();
                            alertDialog.setTitle("Your Profile is Incomplete!");
                            alertDialog.setMessage("Your profile is incomplete. Please provide us more information about you." +
                                    "We want your Date of Birth , Phone no. and PUBG Mobile username and id. Please ignore if you have updated.");
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Update Now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(DashboardActivity1.this, EditProfileActivity.class));
                                }
                            });
                            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Update Later", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                    showAlert();
                                }
                            });
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        }
                    })
                    .show();
    }


    @SuppressLint("IntentReset")
    private void Report(String message) {
        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("text/plain");
        String text = "{" + message + "} I'm getting this message.";
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse("smsto:+919854935115"));
            i.putExtra(Intent.EXTRA_TEXT, text);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

            i.putExtra(Intent.EXTRA_SUBJECT, "PUBCash error report");
            i.putExtra(Intent.EXTRA_TEXT, text);
            i.setData(Uri.parse("mailto:pubcash@blkela.tk"));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.navWallet) {
            startActivity(new Intent(DashboardActivity1.this, WalletActivity.class));
        } else if (id == R.id.navResults) {
            //Snackbar.make(sc, "Coming in next update", Snackbar.LENGTH_LONG).show();
            startActivity(new Intent(DashboardActivity1.this, ResultsActivity.class));
        } else if (id == R.id.navEditprofile) {
            startActivity(new Intent(DashboardActivity1.this, EditProfileActivity.class));
        } else if (id == R.id.navMatches) {
            startActivity(new Intent(DashboardActivity1.this, MatchViewActivity.class));
        } else if (id == R.id.navMyProfile) {
            startActivity(new Intent(DashboardActivity1.this, MyprofileActivity.class));
            //} else if (id == R.id.myResults) {
            //Snackbar.make(sc, "Coming in next update", Snackbar.LENGTH_LONG).show();
        } else if (id == R.id.topPlayer) {
            Snackbar.make(sc, "Coming in next update", Snackbar.LENGTH_LONG).show();
        } else if (id == R.id.navLogout) {
            SharedPrefManager.getInstance(this).logout();
        } else if (id == R.id.mail) {
            EmailSend();
        } else if (id == R.id.whatsapp) {
            WhatsAppMsg();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @SuppressLint("IntentReset")
    private void EmailSend() {
        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "PUBCash Query");
        i.setData(Uri.parse("mailto:pubcash@blkela.tk"));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @SuppressLint("IntentReset")
    private void WhatsAppMsg() {
        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("text/plain");
        i.setData(Uri.parse("smsto:+919854935115"));
        String text = "Write you message";
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            i.setPackage("com.whatsapp");
            i.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Snackbar.make(sc, "WhatsApp is not installed", Snackbar.LENGTH_LONG).show();
        }
    }

    public void showDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void hideDialog() {
        progressDialog.dismiss();
    }

}
