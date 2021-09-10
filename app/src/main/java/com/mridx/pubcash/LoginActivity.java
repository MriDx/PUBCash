package com.mridx.pubcash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.mridx.pubcash.handlers.AppController;
import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.RequestHandler;
import com.mridx.pubcash.handlers.SharedPrefManager;
import com.mridx.pubcash.handlers.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText loginUsername, loginPassword;
    private AppCompatButton btnLogin;
    private AppCompatTextView linkSignup;
    private NestedScrollView sc;
    private ProgressDialog progressDialog;
    private CoordinatorLayout parentLayoutReg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
        btnLogin = findViewById(R.id.btnLogin);


        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hideKeyboard(btnLogin);
                if (!AppController.hasNetwork()) {
                    //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                    Snackbar.make(parentLayoutReg, "No Internet Connection", Snackbar.LENGTH_LONG).show();
                } else{
                    userLogin();
                }

            }
        });

        findViewById(R.id.linkSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                overridePendingTransition(R.anim.from_right, R.anim.to_left);
                finish();

            }
        });
        parentLayoutReg = findViewById(R.id.parentLayoutReg);
        sc = findViewById(R.id.sc);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


    }

    private void userLogin() {

        hideKeyboard(btnLogin);

        final String username = loginUsername.getText().toString();
        final String password = loginPassword.getText().toString();


        if (TextUtils.isEmpty(username)) {
            //loginUsername.setError("Please enter your username!");
            Snackbar.make(parentLayoutReg, "Username can't be empty", Snackbar.LENGTH_SHORT).show();
            loginUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //loginPassword.setError("Please enter correct password!");
            Snackbar.make(parentLayoutReg, "Password can't be empty", Snackbar.LENGTH_SHORT).show();
            loginPassword.requestFocus();
            return;
        }

        class userLogin extends AsyncTask<Void, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog("Signing you in...");
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return requestHandler.sendPostRequest(JavaStrings.URL_LOGIN, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                hideDialog();
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        Snackbar.make(parentLayoutReg, obj.getString("message"), Snackbar.LENGTH_SHORT).show();
                        /*Snackbar.make(parentLayoutReg, "Invalid username or password", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loginUsername.getText().clear();
                                loginPassword.getText().clear();
                                loginUsername.requestFocus();
                            }
                        }).show();*/

                        JSONObject userJson = obj.getJSONObject("user");

                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("fullname"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("gender"),
                                userJson.getString("phone"),
                                userJson.getString("dob"),
                                userJson.getString("token"),
                                userJson.getString("ingamename")

                        );

                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        overridePendingTransition(R.anim.from_left, R.anim.to_right);
                        finish();
                    } else {
                        //Toast.makeText(getApplicationContext(), "Invalid username and password", Toast.LENGTH_SHORT).show();
                        Snackbar.make(sc, obj.getString("Invalid username or password"), Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loginUsername.requestFocus();
                            }
                        }).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        userLogin ul = new userLogin();
        ul.execute();
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

    public void resetPassword(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
    }

}
