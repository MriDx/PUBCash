package com.mridx.pubcash;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.mridx.pubcash.handlers.AppController;
import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.RequestHandler;
import com.mridx.pubcash.handlers.SharedPrefManager;
import com.mridx.pubcash.handlers.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.mridx.pubcash.DashboardActivity.COMPLETEPROFILE;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText editName;
    private TextInputEditText editEmail;
    private TextInputEditText editPhone;
    private static TextInputEditText editDOB;
    private TextInputEditText editOldPassword;
    private TextInputEditText editNewPassword;
    private TextInputEditText editNewPasswordRe;
    private AppCompatButton updateProfile;
    private RadioGroup genderGroup;
    private CircleImageView profilePicView;


    private Toolbar toolbar;

    //for Image chooser activity
    private static int select_photo = 1;
    private static String selectedImgPath;
    private NestedScrollView nes;
    private ProgressDialog progressDialog;
    private AppCompatButton updateGProfile, editResetPassword;
    private TextInputEditText ingamename, ingameid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        nes = findViewById(R.id.nes);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        User user = SharedPrefManager.getInstance(this).getUser();

        editName = (TextInputEditText) findViewById(R.id.editName);
        editName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editName.setText(user.getFullname());
        editEmail = (TextInputEditText) findViewById(R.id.editEmail);
        editEmail.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editEmail.setText(user.getEmail());
        editPhone = (TextInputEditText) findViewById(R.id.editPhone);
        editPhone.setText(user.getPhone());
        editPhone.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editDOB  = (TextInputEditText) findViewById(R.id.editDOB);
        editDOB.setShowSoftInputOnFocus(false);
        editDOB.setFocusable(false);
        editDOB.setText(user.getDob());
        editOldPassword = (TextInputEditText) findViewById(R.id.editOldPassword);
        editNewPassword = (TextInputEditText) findViewById(R.id.editNewPassword);
        editNewPasswordRe = (TextInputEditText) findViewById(R.id.editNewPasswordRe);

        editResetPassword = findViewById(R.id.editResetPassword);
        editResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Checking Passwords...");
                ChangePassword();
            }
        });

        genderGroup = (RadioGroup) findViewById(R.id.editGender);
        AppCompatRadioButton male = genderGroup.findViewById(R.id.editMale);
        String Male = male.getText().toString();

        String gender = user.getGender().toString();
        if (Male.equals(gender) ){
            genderGroup.check(R.id.editMale);
        } else {
            genderGroup.check(R.id.editFemale);
        }

        ingamename = findViewById(R.id.gameName);
        ingamename.setText(user.getIngamename());
        //ingameid = findViewById(R.id.gameId);

        updateGProfile = findViewById(R.id.updateGProfile);
        updateGProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.hasNetwork()) {
                    UpdateGameProfile();
                    showDialog("Updating Game Profile...");
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(EditProfileActivity.this).create();
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
            }
        });

        /*
        profilePicView = (CircleImageView) findViewById(R.id.editAvater);
        profilePicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelector();
            }
        });
        */

        /*editDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        */
        updateProfile = (AppCompatButton) findViewById(R.id.updateProfile);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.hasNetwork()) {
                    UpdateProfile();
                    showDialog("Updating Profile...");
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(EditProfileActivity.this).create();
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

            }
        });

    }


    public void showDatePicker(View v) {
        DialogFragment mFragment = new DatePickerFragment();
        mFragment.show(getSupportFragmentManager(), "datePicker");
    }


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


        @RequiresApi(api = Build.VERSION_CODES.N)
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar mCalender = Calendar.getInstance();
            int year = mCalender.get(Calendar.YEAR);
            int month = mCalender.get(Calendar.MONTH);
            int day = mCalender.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int day) {
            //TextInputEditText editDOB = view.findViewById(R.id.editDOB);
            StringBuilder sb = new StringBuilder();
            String dob = String.valueOf(sb.append(day).append("-").append(month + 1).append("-").append(year));
            editDOB.setText(dob);
            //setDate();
        }

    }


    //profile uapdate method
    private void UpdateProfile() {
        final User user = SharedPrefManager.getInstance(this).getUser();
        final String username = user.getUsername();
        final String fullname = Objects.requireNonNull(editName.getText()).toString();
        final String email = Objects.requireNonNull(editEmail.getText()).toString();
        final String gender = ((AppCompatRadioButton) findViewById(genderGroup.getCheckedRadioButtonId())).getText().toString();
        final String dob = Objects.requireNonNull(editDOB.getText()).toString();
        final String phone = Objects.requireNonNull(editPhone.getText()).toString();

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        class updateProfile extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("fullname", fullname);
                params.put("email", email);
                params.put("phone", phone);
                params.put("dob", dob);
                params.put("gender", gender);
                params.put("token", user.getToken());

                return requestHandler.sendPostRequest(JavaStrings.URL_UPDATE_PROFILE, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jObj = new JSONObject(s);
                    if (!jObj.getBoolean("error")) {
                        JSONObject userJson = jObj.getJSONObject("user");

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

                        hideDialog();

                        Snackbar.make(nes, jObj.getString("message"), Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();


                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        updateProfile up = new updateProfile();
        up.execute();

    }

    //Profile Pic updater
    private void imageSelector() {
        //final int select_photo = 1;

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, select_photo);
        //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == select_photo && resultCode == RESULT_OK && null != data ) {
            Uri selectedImage = data.getData();
            String filePath[] = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            selectedImgPath = c.getString(columnIndex);
            //selectedImgPath = filePath.toString();
            //Toast.makeText(this, selectedImgPath, Toast.LENGTH_LONG).show();

            // UpdateProfilePic();
        }
    }


    private void UpdateGameProfile() {
        @SuppressLint("StaticFieldLeak")
        class updategameprofile extends AsyncTask<Void, Void, String> {
            private final String gamename = Objects.requireNonNull(ingamename.getText()).toString();
            @Override
            protected String doInBackground(Void... voids) {
                final User user = SharedPrefManager.getInstance(EditProfileActivity.this).getUser();
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                params.put("token", user.getToken());
                params.put("ingamename", gamename);
                return requestHandler.sendPostRequest(JavaStrings.URL_GUPDATE, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject object = new JSONObject(s);
                    if (!object.getBoolean("error")) {
                        JSONObject userJson = object.getJSONObject("user");

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
                        hideDialog();
                        Snackbar.make(nes, object.getString("message"), Snackbar.LENGTH_LONG).show();
                        COMPLETEPROFILE = true;
                    } else {
                        hideDialog();
                        Snackbar.make(nes, object.getString("message"), Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        updategameprofile u = new updategameprofile();
        u.execute();
    }

    private void ChangePassword() {
        final User user = SharedPrefManager.getInstance(this).getUser();

        final String oldpass = Objects.requireNonNull(editOldPassword.getText()).toString();
        final String newpass = Objects.requireNonNull(editNewPassword.getText()).toString();
        final String newpassre = Objects.requireNonNull(editNewPasswordRe.getText()).toString();
        class changePassword extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog("Setting-up New-Password...");
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("token", user.getToken());
                params.put("oldpass", oldpass);
                params.put("newpass", newpass);
                return requestHandler.sendPostRequest(JavaStrings.URL_CHANGE_PASS, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                hideDialog();
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        Snackbar.make(nes, obj.getString("message"), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(nes, obj.getString("message"), Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (newpass.equals(newpassre)) {
            if (oldpass.isEmpty() || newpass.isEmpty()) {
                hideDialog();
                Snackbar.make(nes, "Enter Old Password and New Password", Snackbar.LENGTH_LONG).show();
            } else {
                changePassword change = new changePassword();
                change.execute();
            }
        } else {
            hideDialog();
            Snackbar.make(nes, "Password Mismatched", Snackbar.LENGTH_LONG).show();
        }

    }


    /*
    private void UpdateProfilePic() {

        final User user = SharedPrefManager.getInstance(this).getUser();
        final String username = user.getUsername();
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();


        class updateProfilePic extends AsyncTask<Object, String, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Object... voids) {

                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1024 * 1024;
                URL url;
                try {
                    url = new URL(URLs.URL_UPDATE_PROFILE_PIC);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    FileInputStream fileInputStream;
                    DataOutputStream outputStream;
                    outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);

                    outputStream.writeBytes("Content-Disposition: form-data; name=\"reference\""+ lineEnd);
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes("my_refrence_text");
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);

                    outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadFile\";filename=\"" + uri.getLastPathSegment() +"\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);

                    fileInputStream = new FileInputStream(uri.getPath());
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("image", selectedImgPath);
                return requestHandler.sendPostRequest(URLs.URL_UPDATE_PROFILE_PIC, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        alertDialog.setMessage(obj.getString("message"));
                        alertDialog.setCancelable(true);
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        updateProfilePic uPP = new updateProfilePic();
        uPP.execute();

    }
    */

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
        overridePendingTransition(R.anim.from_right, R.anim.to_left);
    }

    public void showDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void hideDialog() {
        progressDialog.hide();
    }

}
