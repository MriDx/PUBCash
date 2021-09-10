package com.mridx.pubcash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.google.android.material.snackbar.Snackbar;
import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.RequestHandler;
import com.mridx.pubcash.handlers.SharedPrefManager;
import com.mridx.pubcash.handlers.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class RegistrationActivity extends AppCompatActivity {

    private EditText fullnameReg, usernameReg, emailReg, passwordReg;
    private AppCompatButton btnSignup;
    private AppCompatTextView linkLogin;
    private RadioGroup genderGroup;
    private CoordinatorLayout parentLayoutReg;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Checking if loggedin or not
       if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }

       fullnameReg = findViewById(R.id.fullnameReg);
       usernameReg = findViewById(R.id.usernameReg);
       emailReg = findViewById(R.id.emailReg);
       passwordReg = findViewById(R.id.passwordReg);
       genderGroup = findViewById(R.id.radioGender);
       btnSignup = findViewById(R.id.btnSignup);
       linkLogin = findViewById(R.id.linkLogin);
       /*
       fullnameReg.onEditorAction(EditorInfo.IME_ACTION_NEXT);
       usernameReg.onEditorAction(EditorInfo.IME_ACTION_NEXT);
       emailReg.onEditorAction(EditorInfo.IME_ACTION_NEXT);
       passwordReg.onEditorAction(EditorInfo.IME_ACTION_DONE);
       */

       progressDialog = new ProgressDialog(this);
       progressDialog.setCancelable(false);

       parentLayoutReg = findViewById(R.id.parentLayoutReg);

       btnSignup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               TakingIn();
               //Snackbar.make(parentLayoutReg, "Test Snackbar", Snackbar.LENGTH_LONG).setAction("Retry", null).show();
           }
       });
       linkLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
               overridePendingTransition(R.anim.from_left, R.anim.to_right);
               finish();
           }
       });


    }

    private void TakingIn() {

        hideKeyboard(btnSignup);

        final String fullname = fullnameReg.getText().toString().trim();
        final String username = usernameReg.getText().toString().trim();
        final String password = passwordReg.getText().toString().trim();
        final String email = emailReg.getText().toString().trim();
        final String gender = ((AppCompatRadioButton) findViewById(genderGroup.getCheckedRadioButtonId())).getText().toString();

        if (TextUtils.isEmpty(fullname)) {
            //fullnameReg.setError("Please enter full name!");
            Snackbar.make(parentLayoutReg, "Name can't be blank", Snackbar.LENGTH_SHORT).show();
            fullnameReg.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            //usernameReg.setError("Please enter Username!");
            Snackbar.make(parentLayoutReg, "Username can't be blank", Snackbar.LENGTH_SHORT).show();
            usernameReg.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //emailReg.setError("Please enter valid Email!");
            Snackbar.make(parentLayoutReg, "Enter a valid Email", Snackbar.LENGTH_SHORT).show();
            emailReg.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //passwordReg.setError("Please enter Password!");
            Snackbar.make(parentLayoutReg, "Password can't be blank", Snackbar.LENGTH_SHORT).show();
            passwordReg.requestFocus();
            return;
        }

        class takingin extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog("Taking you in...");
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("fullname", fullname);
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("gender", gender);
                return requestHandler.sendPostRequest(JavaStrings.URL_REGISTER, params);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                hideDialog();
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        Snackbar.make(parentLayoutReg, obj.getString("message"), Snackbar.LENGTH_SHORT).setAction("Login", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.from_left, R.anim.to_right);
                                finish();
                            }
                        }).show();
                        /*
                        JSONObject userJson = new JSONObject("user");

                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("fullname"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("gender"),
                                userJson.getString("phone"),
                                userJson.getString("dob"),
                                userJson.getString("token")

                        );

                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                        hideDialog();
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                        finish();
                        */
                    } else if (obj.getBoolean("error")){
                        Snackbar.make(parentLayoutReg, obj.getString("message"), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(parentLayoutReg, "Some error occurred", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fullnameReg.requestFocus();
                            }
                        }).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        takingin ru = new takingin();
        ru.execute();


    }

    public void showDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void hideDialog() {
        progressDialog.dismiss();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch(Exception ignored) {
        }
    }

}
