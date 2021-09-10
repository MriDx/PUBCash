package com.mridx.pubcash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.mridx.pubcash.fragments.PaymentPage;
import com.mridx.pubcash.fragments.Transactions;
import com.mridx.pubcash.fragments.Withdraw;
import com.mridx.pubcash.handlers.AppController;
import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.RequestHandler;
import com.mridx.pubcash.handlers.SharedPrefManager;
import com.mridx.pubcash.handlers.TransAdapter;
import com.mridx.pubcash.handlers.User;
import com.mridx.upihandler.UPIHandler;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import instamojo.library.API.TxnVerify;
import instamojo.library.Callback;
import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;

import static com.mridx.pubcash.DashboardActivity.BALANCE;
import static com.mridx.pubcash.handlers.JavaStrings.GPAY_PACKAGE_NAME;
import static com.mridx.pubcash.handlers.JavaStrings.GPAY_REQUEST_CODE;
import static com.mridx.pubcash.handlers.JavaStrings.PHONEPE_PACKAGE_NAME;
import static com.mridx.pubcash.handlers.JavaStrings.PHONEPE_REQUEST_CODE;

public class WalletActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private AppCompatTextView walletBalance;
    private TextInputEditText payAmount;
    private LinearLayout paymentpage;
    private ProgressDialog progressDialog;
    private boolean receiver;

    private LinearLayoutManager layoutManager;

    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> transid = new ArrayList<>();
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<String> credit = new ArrayList<>();
    private ArrayList<String> amount = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;

    private BottomSheetDialog bottomSheetDialog;

    private String mid, cust_id, email, order_id, callback, industry_type, channel_id, txn_amount, checksum, website;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = SharedPrefManager.getInstance(this).getUser();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.my_wallet));

        walletBalance = findViewById(R.id.walletBalance);
        walletBalance.setText(String.valueOf(BALANCE));

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        payAmount = findViewById(R.id.payAmount);

        paymentpage = findViewById(R.id.paymentPage);
        Log.d("layout", String.valueOf(paymentpage));

        //LoadTrans();


    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PaymentPage(), "Add Money");
        adapter.addFragment(new Withdraw(), "Withdraw");
        adapter.addFragment(new Transactions(), "Transactions");
        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



    public void payGpay(View v) {
        payAmount = findViewById(R.id.payAmount);
        String amount = Objects.requireNonNull(payAmount.getText()).toString();
        boolean digitsOnly = TextUtils.isDigitsOnly(amount);
        if (AppController.hasNetwork()) {
            if (digitsOnly) {
                if (amount.length() == 0) {
                    Snackbar.make(paymentpage, "Enter amount to proceed", Snackbar.LENGTH_LONG).show();
                } else if (Integer.parseInt(amount) < 50 || Integer.parseInt(amount) > 200 ){
                    Snackbar.make(paymentpage, "Amount should be minimum of rs 50 or maximum of rs 200.", Snackbar.LENGTH_LONG).show();
                } else {
                    startGPay();
                }
                //payAmount.requestFocus();
                //return;
            } else {
                Snackbar.make(paymentpage, "Enter amount in round figure only, no decimal values are allowed", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(paymentpage, "No Internet Connection", Snackbar.LENGTH_LONG).show();
        }
        hideKeyboard(v);
    }

    private void startGPay() {
        payAmount = findViewById(R.id.payAmount);
        final String amount = Objects.requireNonNull(payAmount.getText()).toString();
        final User user = SharedPrefManager.getInstance(this).getUser();


        class gpay extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                UPIHandler upi = new UPIHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("amount", amount);
                params.put("username", user.getUsername());
                return upi.buildURI(params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(s));
                intent.setPackage(GPAY_PACKAGE_NAME);
                startActivityForResult(intent, GPAY_REQUEST_CODE);

            }
        }
        gpay g = new gpay();
        g.execute();

    }

    public void payPhonepe(View v) {

        payAmount = findViewById(R.id.payAmount);
        String amount = Objects.requireNonNull(payAmount.getText()).toString();
        boolean digitsOnly = TextUtils.isDigitsOnly(amount);
        if (AppController.hasNetwork()) {
            if (digitsOnly) {
                if (amount.length() == 0) {
                    Snackbar.make(paymentpage, "Enter amount to proceed", Snackbar.LENGTH_LONG).show();
                } else {
                    startPhonepe();
                }
                //payAmount.requestFocus();
                //return;
            } else {
                Snackbar.make(paymentpage, "Enter amount in round figure only, no decimal values are allowed", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(paymentpage, "No Internet Connection", Snackbar.LENGTH_LONG).show();
        }
        hideKeyboard(v);
    }

    private void startPhonepe() {
        payAmount = findViewById(R.id.payAmount);
        final String amount = Objects.requireNonNull(payAmount.getText()).toString();
        final User user = SharedPrefManager.getInstance(this).getUser();


        class phonePe extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                UPIHandler upi = new UPIHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("amount", amount);
                params.put("username", user.getUsername());
                return upi.buildURI(params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(s));
                intent.setPackage(PHONEPE_PACKAGE_NAME);
                startActivityForResult(intent, PHONEPE_REQUEST_CODE);

            }
        }
        phonePe phonepe = new phonePe();
        phonepe.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPAY_REQUEST_CODE) {
            if (data != null) {
            Bundle all = data.getExtras();
            String status = Objects.requireNonNull(all).getString("Status");
            if (Objects.requireNonNull(status).equals("FAILURE") || status.equals("Failure") || status.equals("failure")) {
                Snackbar.make(paymentpage, "Payment Failed", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            } else if (status.equals("SUCCESS") || status.equals("Success") || status.equals("success")) {
                RecordPayment(all);
            }
        } else {
            Snackbar.make(paymentpage, "Cancelled by user!", Snackbar.LENGTH_LONG).show();
        }

        } else if (requestCode == PHONEPE_REQUEST_CODE) {
            if (data != null) {
                Bundle all = data.getExtras();
                String status = Objects.requireNonNull(all).getString("Status");
                if (status.equals("Failed") || status.equals("FAILED")) {
                    payAmount.getText().clear();
                    Snackbar.make(paymentpage, "Payment Failed", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                } else if (status.equals("Success") || status.equals("SUCCESS")) {
                    //Toast.makeText(getApplicationContext(), "Payment Completed", Toast.LENGTH_LONG).show();
                    RecordPayment(all);
                } else if (status.equals("Submitted") || status.equals("SUBMITTED")) {
                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                    //submitPhonepe(all);
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setMessage("Your Payment request is not yet confirmed by PhonePe team. If the payment get confirmation status please let us know. Thank you");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            } else {
                Snackbar.make(paymentpage, "Cancelled by user!", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void payInsta(View v) {
        final User user = SharedPrefManager.getInstance(this).getUser();
        payAmount = findViewById(R.id.payAmount);
        String amount = Objects.requireNonNull(payAmount.getText()).toString();
        boolean digitsOnly = TextUtils.isDigitsOnly(amount);
        if (AppController.hasNetwork()) {
            if (digitsOnly) {
                if (amount.length() == 0) {
                    Snackbar.make(paymentpage, "Enter amount to proceed", Snackbar.LENGTH_LONG).show();
                    return;
                } else if (Integer.parseInt(amount) < 50 || Integer.parseInt(amount) > 200) {
                    Snackbar.make(paymentpage, "Amount should be minimum of rs 50 or maximum of rs 200.", Snackbar.LENGTH_LONG).show();
                } else {
                    callInstamojoPay(user.getEmail(), user.getPhone(), amount, "Load Wallet", user.getUsername());
                }
            } else {
                Snackbar.make(paymentpage, "Enter amount in round figure only, no decimal values are allowed", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(paymentpage, "No Internet Connection", Snackbar.LENGTH_LONG).show();
        }
        hideKeyboard(v);
        //final User user = SharedPrefManager.getInstance(this).getUser();
        //payAmount = findViewById(R.id.payAmount);
        //final String amount = Objects.requireNonNull(payAmount.getText()).toString();
        //hideKeyboard(v);
        //callInstamojoPay(user.getEmail(), user.getPhone(), amount, "Load Wallet", user.getUsername());
    }


    private void RecordPayment(Bundle all) {
        final User user = SharedPrefManager.getInstance(this).getUser();
        final String status = all.getString("Status");
        final String transref = all.getString("txnRef");
        final String transid = all.getString("txnId");
        //final String amount1 = all.getString("amount");
        final String amount = Objects.requireNonNull(payAmount.getText()).toString();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        final String paymentDate = dateFormat.format(date);

        class recordpayment extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                params.put("token", user.getToken());
                //params.put("status", status);
                params.put("paymentid", transid);
                params.put("orderid", transref);
                //params.put("amount1", amount1);
                params.put("amount", amount);
                params.put("paymode", "UPI");
                params.put("date", paymentDate);
                params.put("type", "IN");
                params.put("status", "success");
                params.put("network", "UPI");

                return requestHandler.sendPostRequest(JavaStrings.URL_RECORD_PAYMEMT, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        walletBalance.setText(obj.getString("bal"));
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        BALANCE = Integer.parseInt(obj.getString("bal"));
                        //Toast.makeText(getApplicationContext(), "Payment Complete", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Payment update failed", Toast.LENGTH_LONG).show();
                        Snackbar.make(paymentpage, "Unable to verify transaction. Please contact customer support.", Snackbar.LENGTH_LONG)
                                .setAction("Contact now", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        recordpayment rp = new recordpayment();
        rp.execute();

    }

    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        receiver = true;
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            pay.put("amount", amount);
            pay.put("name", buyername);
            pay.put("send_sms", false);
            pay.put("send_email", false);
            // pay.put("webhook", "http://192.168.43.97/androidLogin/webhook_receiver.php");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }

    InstapayListener listener;


    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                showDialog("Fetching Payment Details...");
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                String strWithoutColon = response.replace(":", "\",\"");
                String strWithoutEquals = strWithoutColon.replace("=", "\":\"");
                String perfectJSON = "{\"" + strWithoutEquals + "\"}";
                Log.i("Perfect", perfectJSON);

                try {
                    JSONObject obj = new JSONObject(perfectJSON);
                    String orderId = obj.getString("orderId");
                    String paymentId = obj.getString("paymentId");
                    String token = obj.getString("token");
                    fetchDetails(orderId, paymentId, token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };
    }

    private void fetchDetails(final String orderId, final String paymentId, final String token) {

        TxnVerify verify = new TxnVerify();
        Callback myCallback = new Callback() {
            public void onResponse(String response) {
                // instamojo.library.Instamojo.this.dismissDialogue();
                JSONObject jsonresponse = null;

                try {
                    jsonresponse = new JSONObject(response);
                    if (jsonresponse.getBoolean("status")) {
                        //String e = "success";
                        final String username = jsonresponse.getString("name");
                        final String email = jsonresponse.getString("email");
                        ///final String orderID = orderId);
                        //paymentid from previous parsing
                        final String amount = jsonresponse.getString("amount");
                        final String paymode = jsonresponse.getString("instrument_type");
                        final String network = jsonresponse.getString("billing_instrument");
                        final String time = jsonresponse.getString("created_at");
                        //int bal = Integer.parseInt(amount + BALANCE);
                        //Log.d("bal2", String.valueOf(BALANCE));

                        StartRecord(username, email, orderId, paymentId, amount, paymode, network, time);

                        //String message = "status=" + e + ":orderId=" + orderID + ":txnId=" + transactionID + ":paymentId=" + paymentID + ":token=" + instamojo.library.Instamojo.this.accessToken;
                        //instamojo.library.Instamojo.this.fireBroadcast(10, message);
                    } else {
                        //instamojo.library.Instamojo.this.fireBroadcast(20, "Incorrect Payment Details");
                        Toast.makeText(getApplicationContext(), jsonresponse.getString("status"), Toast.LENGTH_SHORT).show();
                    }

                    //instamojo.library.Instamojo.this.endActivity();
                } catch (JSONException var5) {
                    var5.printStackTrace();
                }
            }
        };

        verify.get("https://api.instamojo.com/" + "v2/payments/", token, paymentId, myCallback); //production
        //verify.get("https://test.instamojo.com/" + "v2/payments/", token, paymentId, myCallback); //test

    }

    private void StartRecord(final String username, final String email, final String orderID, final String paymentId, final String amount, final String paymode, final String network, final String time) {

        @SuppressLint("StaticFieldLeak")
        class startrecord extends AsyncTask<Void, Void, String> {
            final User user = SharedPrefManager.getInstance(WalletActivity.this).getUser();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            final String paymentDate = dateFormat.format(date);

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler reqhandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username",username);
                //params.put("email", email);
                params.put("orderid", orderID);
                params.put("paymentid", paymentId);
                params.put("amount", amount);
                params.put("paymode", paymode);
                params.put("network", network);
                params.put("token", user.getToken());
                params.put("date", paymentDate);
                params.put("type", "IN");
                params.put("status", "success");
                return reqhandler.sendPostRequest(JavaStrings.URL_RECORD_PAYMEMT, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                hideDialog();
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        //if error is false
                        //do something
                        walletBalance.setText(obj.getString("bal"));
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        BALANCE = Integer.parseInt(obj.getString("bal"));
                    } else {
                        // record can not be inserted
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        startrecord start = new startrecord();
        start.execute();

    }


    private void showDialog(String message) {
        this.progressDialog.setMessage(message);
        this.progressDialog.show();
    }

    public void hideDialog() {
        this.progressDialog.dismiss();
    }

    /*
    @Override
    protected void onPause() {
        super.onPause();
        InstamojoPay instamojoPay = new InstamojoPay();
        if (receiver) {
            unregisterReceiver(instamojoPay);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        InstamojoPay instamojoPay = new InstamojoPay();
        if (receiver) {
            unregisterReceiver(instamojoPay);
        }
    }
    */



    public void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
        } else {
            overridePendingTransition(R.anim.from_left, R.anim.to_right);
            finish();
        }

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



    public void paytmWithdraw(View v) {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.paytm);
        bottomSheetDialog.show();
        final ConstraintLayout wd1 = bottomSheetDialog.findViewById(R.id.wd1);
        AppCompatButton paytmWD = bottomSheetDialog.findViewById(R.id.paytmWD);
        Objects.requireNonNull(paytmWD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText paytmNo = bottomSheetDialog.findViewById(R.id.paytmNo);
                TextInputEditText paytmAmount = bottomSheetDialog.findViewById(R.id.paytmAmount);
                String paytmno = Objects.requireNonNull(paytmNo).getText().toString();
                String paytmamount = Objects.requireNonNull(paytmAmount.getText()).toString();
                boolean digitsOnly = TextUtils.isDigitsOnly(paytmno);
                boolean am = TextUtils.isDigitsOnly(paytmamount);
                if (AppController.hasNetwork()) {
                    if (paytmamount.isEmpty() || paytmno.isEmpty()) {
                        Toast.makeText(WalletActivity.this, "Enter Details", Toast.LENGTH_SHORT).show();
                    } else if (digitsOnly || am) {
                        int amount = Integer.parseInt(paytmamount);
                        if (BALANCE >= amount) {
                            if (amount >= 100) {
                                if (paytmno.length() == 10) {
                                    //Toast.makeText(WalletActivity.this, "Proceed", Toast.LENGTH_SHORT).show();
                                    ProceedPaytm(paytmamount, paytmno);
                                } else {
                                    Toast.makeText(WalletActivity.this, "Enter paytm no", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(WalletActivity.this, "Minimum is 100", Toast.LENGTH_SHORT).show();
                                //paytmAmount.setError("Minimum withdraw is 100");
                            }
                        } else {
                            //Snackbar.make(bottomSheetDialog, "Low balance", Snackbar.LENGTH_INDEFINITE).show();
                            //paytmAmount.setError("Low balance");
                            Toast.makeText(WalletActivity.this, "Low Balance", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(WalletActivity.this, "Enter again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WalletActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ProceedPaytm(final String amount, final String paytmno) {
        final User user = SharedPrefManager.getInstance(this).getUser();
        final int bal = BALANCE - Integer.parseInt(amount);
        //final AppCompatTextView walletBalance = findViewById(R.id.walletBalance);
        //BALANCE = bal;
        //walletBalance.setText(String.valueOf(BALANCE));
        @SuppressLint("StaticFieldLeak")
        class proceedPaytm extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                params.put("token", user.getToken());
                params.put("amount", amount);
                params.put("paytm", paytmno);
                params.put("bal", String.valueOf(bal));
                params.put("paymode", "PayTM");
                return requestHandler.sendPostRequest(JavaStrings.URL_PAYTM_WITHDRAW, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        bottomSheetDialog.dismiss();
                        BALANCE = bal;
                        walletBalance.setText(obj.getString("bal"));
                        final AlertDialog alertDialog = new AlertDialog.Builder(WalletActivity.this).create();
                        alertDialog.setTitle("Withdraw ");
                        alertDialog.setMessage(obj.getString("message"));
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    } else {
                        bottomSheetDialog.dismiss();
                        final AlertDialog alertDialog = new AlertDialog.Builder(WalletActivity.this).create();
                        alertDialog.setTitle("Withdraw ");
                        alertDialog.setMessage(obj.getString("message"));
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //BALANCE = bal;
                //walletBalance.setText(String.valueOf(BALANCE));
            }
        }
        proceedPaytm p = new proceedPaytm();
        p.execute();
    }

    public void upiWithdraw(View v) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.upi);
        bottomSheetDialog.show();
    }
    public void bankWithdraw(View v) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bank);
        bottomSheetDialog.show();
    }

    public void showToast(View v) {
        Toast.makeText(WalletActivity.this, "Working", Toast.LENGTH_SHORT).show();
    }

    public void paytmWDP(View v) {
        TextInputEditText paytmNo = findViewById(R.id.paytmNo);
        TextInputEditText paytmAmount = findViewById(R.id.paytmAmount);
        String paytmno = paytmNo.getText().toString();
        String paytmamount = paytmAmount.getText().toString();
        boolean digitsOnly = TextUtils.isDigitsOnly(paytmno);
        boolean am = TextUtils.isDigitsOnly(paytmamount);
        if (AppController.hasNetwork()) {
            if (digitsOnly || am) {
                if ((paytmno.length() == 10) || Integer.parseInt(paytmamount) <= BALANCE || Integer.parseInt(paytmamount) >= 100) {
                    Toast.makeText(WalletActivity.this, "Can Proceed", Toast.LENGTH_SHORT).show();
                } else {
                    //Snackbar.make(paymentpage, "Check your PayTM no. or Amount.", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(WalletActivity.this, "PayTM no. is not valid.", Toast.LENGTH_SHORT).show();
                }
            } else {
                //Snackbar.make(paymentpage, "Enter amount in round figure only, no decimal values are allowed", Snackbar.LENGTH_LONG).show();
                Toast.makeText(WalletActivity.this, "Enter Amount or PayTM no. is incorrect", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Snackbar.make(paymentpage, "No Internet Connection", Snackbar.LENGTH_LONG).show();
            Toast.makeText(WalletActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void showSnackbar(View v) {
        Snackbar.make(paymentpage, "This option will enable soon", Snackbar.LENGTH_LONG).show();
    }


    //Paytm Payment Gateway

    public void payPaytm(View v) {
        payAmount = findViewById(R.id.payAmount);
        String amount = Objects.requireNonNull(payAmount.getText()).toString();
        boolean digitsOnly = TextUtils.isDigitsOnly(amount);
        if (AppController.hasNetwork()) {
            if (digitsOnly) {
                if (amount.length() == 0) {
                    Snackbar.make(paymentpage, "Enter amount to proceed", Snackbar.LENGTH_LONG).show();
                    return;
                } else if (Integer.parseInt(amount) < 50 || Integer.parseInt(amount) > 500) {
                    Snackbar.make(paymentpage, "Amount should be minimum of rs 50 or maximum of rs 500.", Snackbar.LENGTH_LONG).show();
                } else {
                    PaytmPayment();
                }
            } else {
                Snackbar.make(paymentpage, "Enter amount in round figure only, no decimal values are allowed", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(paymentpage, "No Internet Connection", Snackbar.LENGTH_LONG).show();
        }
        hideKeyboard(v);
    }

    private void PaytmPayment() {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");
        Date date = new Date();
        String d = dateFormat.format(date);
        Random r = new Random(System.currentTimeMillis());
        final String orderId = "ORDER" + d + (1 + r.nextInt(2)) * 10 + r.nextInt(10);

        final User user = SharedPrefManager.getInstance(this).getUser();
        final String username = user.getUsername();
        final String useremail = user.getEmail();
        payAmount = findViewById(R.id.payAmount);
        final String amount = Objects.requireNonNull(payAmount.getText()).toString().trim();

        final PaytmPGService Service = PaytmPGService.getProductionService();
        //final PaytmPGService Service = PaytmPGService.getStagingService();

        class paytmPayment extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("orderid", orderId);
                params.put("amount", amount);
                params.put("email", useremail);
                params.put("custid", user.getEmail());
                return requestHandler.sendPostRequest(JavaStrings.URL_GENERATECHECKSUM, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    mid = obj.getString("MID");
                    order_id = obj.getString("ORDER_ID");
                    cust_id = obj.getString("CUST_ID");
                    callback = obj.getString("CALLBACK_URL");
                    industry_type = obj.getString("INDUSTRY_TYPE_ID");
                    channel_id = obj.getString("CHANNEL_ID");
                    txn_amount = obj.getString("TXN_AMOUNT");
                    checksum = obj.getString("CHECKSUMHASH");
                    //mobile = obj.getString("MOBILE_NO");
                    email = obj.getString("EMAIL");
                    website = obj.getString("WEBSITE");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HashMap<String, String> paramMap = new HashMap<>();
                paramMap.put("MID", mid);
                paramMap.put("ORDER_ID", order_id);
                paramMap.put("CUST_ID", cust_id);
                paramMap.put("INDUSTRY_TYPE_ID",industry_type);
                paramMap.put("CHANNEL_ID",channel_id);
                paramMap.put("TXN_AMOUNT",txn_amount);
                paramMap.put("WEBSITE", website);
                paramMap.put("EMAIL",email);
                //paramMap.put("MOBILE_NO",mobile);
                paramMap.put("CALLBACK_URL",callback);
                paramMap.put("CHECKSUMHASH",checksum);

                PaytmOrder Order = new PaytmOrder(paramMap);

                Service.initialize(Order, null);

                Service.startPaymentTransaction(WalletActivity.this, true, true,
                        new PaytmPaymentTransactionCallback() {

                            @Override
                            public void someUIErrorOccurred(String inErrorMessage) {
                                //Toast.makeText(getApplicationContext(), "Payment Transaction response " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                                Snackbar.make(paymentpage, inErrorMessage.toString(), Snackbar.LENGTH_LONG).show();
                            }

                            @Override
                            public void onTransactionResponse(Bundle inResponse) {
                                Log.d("LOG", "Payment Transaction :" + inResponse);

                                String response=inResponse.getString("RESPMSG");
                                if (response.equals("Txn Success")) {
                                    //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                                    RecordPaytmPayment(inResponse);
                                } else {
                                    //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                                    Snackbar.make(paymentpage, response, Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).show();
                                }

                            }

                            @Override
                            public void networkNotAvailable() {
                                Snackbar.make(paymentpage, "Network Error!", Snackbar.LENGTH_LONG).show();

                            }

                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {
                                //Toast.makeText(getApplicationContext(), "clientAuthenticationFailed"+inErrorMessage.toString() , Toast.LENGTH_LONG).show();
                                Snackbar.make(paymentpage, "Payment Failed! Contact us for further information.", Snackbar.LENGTH_LONG)
                                        .setAction("Contact us", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //add contsct us
                                            }
                                        }).show();
                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode,
                                                              String inErrorMessage, String inFailingUrl) {

                                //Toast.makeText(getApplicationContext(), "onErrorLoadingWebPage"+inErrorMessage.toString() , Toast.LENGTH_LONG).show();
                                Snackbar.make(paymentpage, inErrorMessage, Snackbar.LENGTH_LONG).show();
                            }

                            // had to be added: NOTE
                            @Override
                            public void onBackPressedCancelTransaction() {
                                //Toast.makeText(getApplicationContext(), "onBackPressedCancelTransaction", Toast.LENGTH_LONG).show();
                                Snackbar.make(paymentpage, "Cancelled by user", Snackbar.LENGTH_LONG).show();
                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage,
                                                            Bundle inResponse) {
                                Log.d("LOG", "Payment Transaction Failed "
                                        + inErrorMessage);
                                //Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                                Snackbar.make(paymentpage, "Transaction Cancelled by User", Snackbar.LENGTH_LONG).show();
                            }

                        });
            }

            }

        paytmPayment paytmPayment = new paytmPayment();
        paytmPayment.execute();
    }

    private void RecordPaytmPayment(Bundle inResponse) {
        final String OrderID = inResponse.getString("ORDERID");
        final String TXNID = inResponse.getString("TXNID");
        final String PAYMODE = inResponse.getString("PAYMENTMODE");
        final String AMOUNT = inResponse.getString("TXNAMOUNT");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        final String paymentDate = dateFormat.format(date);
        class recordPaytmPayment extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                params.put("token", user.getToken());
                //params.put("status", status);
                params.put("paymentid", TXNID);
                params.put("orderid", OrderID);
                //params.put("amount1", amount1);
                params.put("amount", AMOUNT);
                params.put("paymode", PAYMODE);
                params.put("date", paymentDate);
                params.put("type", "IN");
                params.put("status", "success");
                params.put("network", "PAYTM");
                return requestHandler.sendPostRequest(JavaStrings.URL_RECORD_PAYMEMT, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        walletBalance.setText(obj.getString("bal"));
                        //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        Snackbar.make(paymentpage, obj.getString("message"), Snackbar.LENGTH_LONG).show();
                        BALANCE = Integer.parseInt(obj.getString("bal"));
                        //Toast.makeText(getApplicationContext(), "Payment Complete", Toast.LENGTH_LONG).show();
                    } else {
                        //Toast.makeText(getApplicationContext(), "Payment update failed", Toast.LENGTH_LONG).show();
                        Snackbar.make(paymentpage, "Unable to verify transaction. Please contact customer support.", Snackbar.LENGTH_LONG)
                                .setAction("Contact now", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        recordPaytmPayment recordPaytmPayment = new recordPaytmPayment();
        recordPaytmPayment.execute();
    }

}
