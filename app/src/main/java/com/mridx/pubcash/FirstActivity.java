package com.mridx.pubcash;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.Environment.getExternalStorageDirectory;

public class FirstActivity extends AppCompatActivity {

    private ProgressBar firstProgress;

    private ProgressDialog progressDialog;

    public boolean SERVER;

    private ConstraintLayout parentLayout;

    private AppCompatTextView firstNotice;

    private boolean isDownloading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        firstProgress = findViewById(R.id.firstProgress);
        firstProgress.setIndeterminate(true);

        parentLayout = findViewById(R.id.parentLayout);

        firstNotice = findViewById(R.id.firstNotice);


        FirebaseMessaging.getInstance().subscribeToTopic("INSTALLED").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("FirstActivity", "Subscribed to Intslled");
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CheckUpdate();
            }
        }, 2000);

        //CheckingServer();
        //CheckUpdate();

    }


    private void CheckUpdate() {

        showNotice("Checking for update...");

        //CheckPermissions();

        final AlertDialog alertDialog = new AlertDialog.Builder(FirstActivity.this).create();

        class checkUpdate extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                //final AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
                final String ver = BuildConfig.VERSION_NAME;
                //final String ver = "0.3";
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("currentver", ver);
                return requestHandler.sendPostRequest(JavaStrings.URL_CHECK_UPDATE, params);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    final JSONObject obj = new JSONObject(s);
                    firstProgress.setVisibility(View.GONE);
                    hideNotice();
                    if (!obj.getBoolean("error")) {
                        final String link = obj.getString("link");
                        final String whatsnew = obj.getString("new");
                        String n = whatsnew.replace(":", "\n");
                        alertDialog.setTitle(obj.getString("title"));
                        //alertDialog.setMessage(obj.getString("message"));
                        alertDialog.setMessage("Version : " + obj.getString("newver")  + n);
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, obj.getString("button"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                                try {
                                    if (obj.getString("button").equals("Download")) {
                                        if (ActivityCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ActivityCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        ) {
                                            AskForPermission();
                                        } else {
                                            DownloadUpdate(obj.getString("link"));
                                        }

                                        //DownloadUpdate(obj.getString("link"));

                                    } else {
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        });/*
                        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Read More", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://noobdevs.tk/pubcash/whats-new/")));
                            }
                        });*/
                        alertDialog.show();
                    } else {
                        startActivity(new Intent(FirstActivity.this, LoginActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        checkUpdate cUpdate = new checkUpdate();
        cUpdate.execute();

    }

    private void AskForPermission() {
        Dexter.withActivity(FirstActivity.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            CheckUpdate();
                        } else if (report.isAnyPermissionPermanentlyDenied()){
                            PermissionListener dialogPermissionListener =
                                    DialogOnDeniedPermissionListener.Builder
                                            .withContext(FirstActivity.this)
                                            .withTitle("Storage Permission")
                                            .withMessage("App can't be updated without storage permission")
                                            .withButtonText(android.R.string.ok)
                                            .withIcon(R.mipmap.ic_launcher)
                                            .build();
                        } else if (!report.areAllPermissionsGranted()) {
                            PermissionListener dialogPermissionListener =
                                    DialogOnDeniedPermissionListener.Builder
                                            .withContext(FirstActivity.this)
                                            .withTitle("Storage Permission")
                                            .withMessage("App can't be updated without storage permission")
                                            .withButtonText(android.R.string.ok)
                                            .withIcon(R.mipmap.ic_launcher)
                                            .build();
                            AskForPermission();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {

                    }
                })
                .onSameThread()
                .check();
    }

    private void DownloadUpdate(String link) {

        //checkStoragePermission();

        isDownloading = true;

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading...");
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(link)
                .build();

        OutputStream stream = null;
        try {
            stream = new FileOutputStream(getExternalStorageDirectory() + "/Download/PUBCashLatest.apk");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            isDownloading = false;
        }


        final OutputStream finalStream = stream;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] data = new byte[8192];
                float total = 0;
                int read_bytes = 0;
                float fileSize = response.body().contentLength();

                InputStream inputStream = response.body().byteStream();

                while ((read_bytes = inputStream.read(data)) != -1) {
                    total = total + read_bytes;
                    finalStream.write(data, 0, read_bytes);
                    progressDialog.setProgress((int) ((total / fileSize)* 100));
                }

                progressDialog.dismiss();
                inputStream.close();
                finalStream.close();

                isDownloading = false;

                installApp();
                finish();
                //File fileLocation = new File(getExternalStorageDirectory() + "/MriDx/new.apk");

                /*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Context context = getApplicationContext();
                    //Uri apkUri = FileProvider.getUriForFile(SplashActivity.this, "com.example.mridx.provider",  fileLocation);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(FileProvider.getUriForFile(context.getApplicationContext(), "com.example.mridx.provider",  fileLocation), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                } else {
                    Uri apkUri = Uri.fromFile(fileLocation);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
                */

            }


        });
    }

    private void installApp() {

        File file = new File(getExternalStorageDirectory()+"/Download/PUBCashLatest.apk");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Context context = FirstActivity.this;
            //Uri apkUri = FileProvider.getUriForFile(SplashActivity.this, "com.example.mridx.provider",  fileLocation);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(FileProvider.getUriForFile(context,
                    "com.mridx.pubcash.FileProvider",
                    file ),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
            finish();
        } else {
            Uri apkUri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
            finish();
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isDownloading) {
            CheckUpdate();
        }

    }

    public void showNotice(String message) {
        firstNotice.setText(message);
        firstNotice.setVisibility(View.VISIBLE);
    }

    public void hideNotice() {
        firstNotice.setVisibility(View.GONE);
    }
}
