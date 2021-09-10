package com.mridx.pubcash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.RequestHandler;
import com.mridx.pubcash.handlers.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ForgotPassword extends AppCompatActivity {

    private TextInputEditText forgotName, forgotEmail;
    private AppCompatButton recover;

    private BottomSheetDialog bottomSheetDialog;
    private ConstraintLayout forgotParent;

    private TextInputEditText otp, password;

    private Toolbar toolbar;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        forgotName = findViewById(R.id.forgotName);
        forgotEmail = findViewById(R.id.forgotEmail);
        recover = findViewById(R.id.recover);

        forgotParent = findViewById(R.id.forgotParent);



        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecoverPassword();
            }
        });
    }

    private void RecoverPassword() {
        showDialog("Sending OTP...");
        final String fullname = Objects.requireNonNull(forgotName.getText()).toString();
        final String email = Objects.requireNonNull(forgotEmail.getText()).toString();

        class recoverPassword extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("fullname", fullname);
                params.put("email", email);
                return requestHandler.sendPostRequest(JavaStrings.URL_PASSWORD_RECOVERY, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                hideDialog();
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        showBtmSheet();
                    } else {
                        Snackbar.make(forgotParent, obj.getString("message"), Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        recoverPassword recoverPassword = new recoverPassword();
        recoverPassword.execute();
    }

    private void showBtmSheet() {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.password_enter_btmsheet);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();

        ImageButton close = bottomSheetDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        otp = bottomSheetDialog.findViewById(R.id.otp);
        password = bottomSheetDialog.findViewById(R.id.newPass);



        AppCompatButton resetBtn = bottomSheetDialog.findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    Toast.makeText(ForgotPassword.this, "OTP and New Password should not be empty", Toast.LENGTH_LONG).show();
                } else {
                    startVerify();
                }
            }
        });
    }

    private void startVerify() {
        final String OTP = otp.getText().toString().trim();
        final String PASSWORD = password.getText().toString().trim();
        showDialog("Verifying OTP...");
        class startverify extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("otp", OTP);
                params.put("password", PASSWORD);
                return requestHandler.sendPostRequest(JavaStrings.URL_PASSWORD_VERIFY, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                hideDialog();
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        bottomSheetDialog.dismiss();
                        Snackbar.make(forgotParent, obj.getString("message"), Snackbar.LENGTH_INDEFINITE)
                                .setAction("Login", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                                        finish();
                                    }
                                })
                                .show();
                    } else {
                        Toast.makeText(ForgotPassword.this, "OTP is incorrect", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        startverify startverify = new startverify();
        startverify.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemid = item.getItemId();
        switch (itemid) {
            case android.R.id.home:
                onBackPressed();
                finish();
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

    public void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch(Exception ignored) {
        }
    }

}
