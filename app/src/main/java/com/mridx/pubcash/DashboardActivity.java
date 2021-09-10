package com.mridx.pubcash;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mridx.pubcash.handlers.AppController;
import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.RequestHandler;
import com.mridx.pubcash.handlers.SharedPrefManager;
import com.mridx.pubcash.handlers.SwipeTouchTracer;
import com.mridx.pubcash.handlers.User;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.mridx.pubcash.DashboardActivity1.NOTICE;
import static com.mridx.pubcash.handlers.JavaStrings.PHONEPE_PACKAGE_NAME;
import static com.mridx.pubcash.handlers.JavaStrings.PHONEPE_REQUEST_CODE;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NestedScrollView sc;
    AppCompatTextView nameView, userNameView, balView;
    TextView mPlayed, mKill, mWin;
    AppCompatButton editMyProfile, joinMatch, viewResults, myWallet;
    CircleImageView profilePicView;
    private TextInputEditText notTitle, notMessage;
    private CoordinatorLayout co;
    private Toolbar toolbar;
    private FloatingActionButton editProfileFab;
    public static Boolean COMPLETEPROFILE;
    public static Boolean SHOWN;

    public static int BALANCE = 0;
    public static String COMPLETE = "true";

    private ProgressDialog progressDialog;

    public static String HELP;

    private BottomSheetDialog updateProfileSheet;
    private TextInputEditText phoneNo, updateDob, updateIGName;
    private AppCompatButton update;

    private AppCompatTextView noticeBody;

    private User user;
    private String username, token, className;

    public static boolean DASHBOARDACTIVITY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        className = getClass().getSimpleName();

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        showDialog("Loading...");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseInAppMessaging inAppMessaging = FirebaseInAppMessaging.getInstance();
        inAppMessaging.setAutomaticDataCollectionEnabled(true);
        inAppMessaging.setMessagesSuppressed(false);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String instanceID = instanceIdResult.getId();
                Log.d("instanceID", instanceID);
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("MAIN").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("topic", "Subscribed to Notification Topic");
            }
        });

        FirebaseMessaging.getInstance().unsubscribeFromTopic("INSTALLED").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("topic", "Unsubscribed from Installed");
            }
        });

        user = SharedPrefManager.getInstance(this).getUser();
        username = user.getUsername();
        token = user.getToken();

        /*completeProfileAlert = findViewById(R.id.completeProfileAlert);
        completeProfileAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });
        */
        COMPLETEPROFILE = true;

        noticeBody = findViewById(R.id.noticeBody);

        //TextView n = navigationView.getHeaderView(0).findViewById(R.id.appName);
        //n.setText(R.string.app_name);

        sc = findViewById(R.id.sc);
        sc.setOnTouchListener(new SwipeTouchTracer(this){
            @Override
            public void onSwipeLeft() {
                //Snackbar.make(sc, "Left Swipe", Snackbar.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, WalletActivity.class));
                overridePendingTransition(R.anim.from_right, R.anim.to_left);
            }
            @Override
            public void onSwipeRight() {
                //Snackbar.make(sc, "Right Swipe", Snackbar.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, MatchViewActivity.class));
                overridePendingTransition(R.anim.from_left, R.anim.to_right);
            }
        });

        //Floating Action Button for edit profile
        /*editProfileFab = findViewById(R.id.editProfileFab);
        editProfileFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, EditProfileActivity.class));
            }
        });*/


        //CheckRoom();

        //CheckUpdate();

        if (AppController.hasNetwork()) {
            if (HELP == null) {
                GetHelpLink();
            }

            LoadNotice();
        }

        /*
        if (HELP == null) {
            GetHelpLink();
        }

        //StartActivity();
        LoadNotice();
        */
    }

    private void StartActivity() {

        showDialog("Loading...");

        nameView = (AppCompatTextView) findViewById(R.id.nameView);
        //userNameView = (AppCompatTextView) findViewById(R.id.userNameView);
        balView = (AppCompatTextView) findViewById(R.id.balView);

        final User user = SharedPrefManager.getInstance(this).getUser();
        nameView.setText(user.getFullname());
        //userNameView.setText(user.getUsername());
        profilePicView = findViewById(R.id.dashboardProfileImage);
        String gender = user.getGender();
        //Picasso.with(DashboardActivity.this).load(JavaStrings.URL_PROFILEIMAGE + gender +".png").memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(profilePicView);

        Picasso.with(DashboardActivity.this)
                .load(JavaStrings.FIREBASE_ROOT + gender +".png" + JavaStrings.FIREBASE_END)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(profilePicView);

        mPlayed = findViewById(R.id.mPlayed);
        mKill = findViewById(R.id.mKill);
        mWin = findViewById(R.id.mWin);

        if (!AppController.hasNetwork()) {
            Snackbar.make(sc, "No Internet Connection", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRestart();
                }
            }).show();
            hideDialog();
        }

        //Retriving Balance

        //getBalance();
        //CheckProfile();

        LoadAll();

    }

/*
    private void getBalance() {

        final User user = SharedPrefManager.getInstance(this).getUser();

        class getbalance extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler reqhandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                params.put("token", user.getToken());
                return reqhandler.sendPostRequest(JavaStrings.URL_GET_BAL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        if (!obj.getString("amount").equals("")) {
                            //set balance
                            balView.setText(obj.getString("amount"));
                            BALANCE = Integer.parseInt(obj.getString("amount"));
                            //BALANCE = obj.getString("amount");
                            GetAdditional();
                        } else {
                            BALANCE = 0;
                            balView.setText(String.valueOf(BALANCE));
                            Log.d("bal", String.valueOf(BALANCE));
                            GetAdditional();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        getbalance getbal = new getbalance();
        getbal.execute();

    }

    private void GetAdditional() {

        final User user = SharedPrefManager.getInstance(this).getUser();

        class getAddictional extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                return requestHandler.sendPostRequest(JavaStrings.URL_LOAD_ADD1, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        JSONObject result = obj.getJSONObject("result");
                        mPlayed.setText(result.getString("t_join"));
                        mKill.setText(result.getString("t_kill"));
                        mWin.setText(result.getString("t_won"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CheckProfile();

            }
        }
        getAddictional getadd = new getAddictional();
        getadd.execute();
    }
*/

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
                        String balance = prop.getString("balance");
                        balView.setText(prop.getString("balance"));
                        mKill.setText(prop.getString("t_kill"));
                        mWin.setText(prop.getString("t_won"));
                        mPlayed.setText(prop.getString("t_join"));

                        BALANCE = Integer.parseInt(balance);

                        CheckProfile();
                    } else {
                        hideDialog();
                        final String message = obj.getString("message");
                        Snackbar.make(sc, message, Snackbar.LENGTH_INDEFINITE)
                                .setAction("Report", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Call Report
                                        Report(message);
                                    }
                                })
                                .show();
                        BALANCE = 0;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        loadAll loadAll = new loadAll();
        loadAll.execute();
    }

    @SuppressLint("IntentReset")
    private void Report(String message) {
        //PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("text/plain");
        String text = "{" + message + "} I'm getting this message on " + className ;
        i.putExtra(Intent.EXTRA_SUBJECT, "PUBCash error report");
        i.putExtra(Intent.EXTRA_TEXT, text);
        i.setData(Uri.parse("mailto:pubcash@blkela.tk"));
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        /*
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
        }*/

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //StartActivity();
    }



    // check for update
    private void CheckUpdate() {

        class checkUpdate extends AsyncTask<Void, Void, String> {


            ProgressDialog pDialog;
            final String currentVer = "1.0";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("current", currentVer);
                return null;
            }
        }

        checkUpdate cUpdate = new checkUpdate();
        cUpdate.execute();

    }

    private void CheckProfile() {
        //Need in MatchDetails.java
        User user = SharedPrefManager.getInstance(this).getUser();
        if (user.getDob().isEmpty() || user.getPhone().isEmpty()) {
            //completeProfileAlert.setVisibility(View.VISIBLE);
            COMPLETEPROFILE = false;
            COMPLETE = "false";
            showAlert();
        } else if (user.getIngamename().isEmpty()) {
            //completeProfileAlert.setVisibility(View.VISIBLE);
            COMPLETEPROFILE = false;
            COMPLETE = "false";
            showAlert();
        }

        hideDialog();

    }

    private void showAlert() {
        User user = SharedPrefManager.getInstance(this).getUser();
        if (user.getDob().equals("") || user.getPhone().equals("")) {
            Snackbar.make(sc, "Profile is incomplete. Please update your profile", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Know More", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(DashboardActivity.this).create();
                            alertDialog.setTitle("Your Profile is Incomplete!");
                            alertDialog.setMessage("Your profile is incomplete. Please provide us more information about you." +
                                    "We want your Date of Birth , Phone no. and PUBG Mobile username and id.");
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Update Now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(DashboardActivity.this, EditProfileActivity.class));
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
        } else if (user.getIngamename().equals("")) {
            Snackbar.make(sc, "Profile is incomplete. Please update your profile", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Know More", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(DashboardActivity.this).create();
                            alertDialog.setTitle("Your Profile is Incomplete!");
                            alertDialog.setMessage("Your profile is incomplete. Please provide us more information about you." +
                                    "We want your Date of Birth , Phone no. and PUBG Mobile username and id. Please ignore if you have updated.");
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Update Now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(DashboardActivity.this, EditProfileActivity.class));
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
    }


    private void showBtmShtDlg() {
        updateProfileSheet = new BottomSheetDialog(this);
        updateProfileSheet.setContentView(R.layout.update_profile);
        updateProfileSheet.show();
        update = updateProfileSheet.findViewById(R.id.update);
        updateDob = updateProfileSheet.findViewById(R.id.updateDOB);
        updateIGName = updateProfileSheet.findViewById(R.id.updateIGName);
        phoneNo = updateProfileSheet.findViewById(R.id.updatePhone);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int itemid = item.getItemId();
        switch (itemid) {
            case R.id.logout:
                //Snackbar.make(sc, "Logout", Snackbar.LENGTH_SHORT).show();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                //startActivity(new Intent(this, LoginActivity.class));
                finish();
            break;
            case R.id.help:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(HELP));
                intent.setPackage("com.google.android.youtube");
                startActivity(intent);
            break;
        }
        return true;
    }


    public void toogleEdit(View v) {
        if (editProfileFab.isShown()) {
            editProfileFab.hide();
            Snackbar.make(sc, "Edit Profile", Snackbar.LENGTH_SHORT).show();
        } else {
            editProfileFab.show();
            //startActivity(new Intent(DashboardActivity.this, EditProfileActivity.class));

        }
    }

    private void GetHelpLink() {
        class getHelpLink extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("title", "help");
                return requestHandler.sendPostRequest(JavaStrings.URL_GET_HELP_LINKS, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        HELP = obj.getString("link");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        getHelpLink getHelp = new getHelpLink();
        getHelp.execute();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.navWallet) {
            startActivity(new Intent(DashboardActivity.this, WalletActivity.class));
        } else if (id == R.id.navResults) {
            //Snackbar.make(sc, "Coming in next update", Snackbar.LENGTH_LONG).show();
            startActivity(new Intent(DashboardActivity.this, ResultsActivity.class));
        } else if (id == R.id.navEditprofile) {
            startActivity(new Intent(DashboardActivity.this, EditProfileActivity.class));
        } else if (id == R.id.navMatches) {
            startActivity(new Intent(DashboardActivity.this, MatchViewActivity.class));
        } else if (id == R.id.navMyProfile) {
            startActivity(new Intent(DashboardActivity.this, MyprofileActivity.class));
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

    public void yt(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.youtube.com/channel/UCO7VwloEbW2BfCaWzyLBvug"));
        //intent.setPackage("com.google.android.youtube");
        startActivity(intent);
    }

    public void ig(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://instagram.com/pubcash8"));
        //intent.setPackage("com.google.android.youtube");
        startActivity(intent);
    }

    public void fb(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://facebook.com/pubcashofficial"));
        //intent.setPackage("com.google.android.youtube");
        startActivity(intent);
    }

    public void dc(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://discord.gg/YFZZjbd"));
        //intent.setPackage("com.google.android.youtube");
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //LoadNotice();
        StartActivity();
    }

    private void LoadNotice() {
        class loadNotice extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "new");
                return requestHandler.sendPostRequest(JavaStrings.URL_LOAD_NOTICE, params);
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
    protected void onPause() {
        super.onPause();
        DASHBOARDACTIVITY = false;
        Log.d("info", "activity paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        DASHBOARDACTIVITY = false;
        Log.d("info", "activity Stop");

    }



}
