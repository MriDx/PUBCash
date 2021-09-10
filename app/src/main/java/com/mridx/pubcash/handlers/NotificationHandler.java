package com.mridx.pubcash.handlers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mridx.pubcash.DashboardActivity;
import com.mridx.pubcash.FirstActivity;
import com.mridx.pubcash.MatchViewActivity;
import com.mridx.pubcash.R;
import com.mridx.pubcash.ResultsActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.mridx.pubcash.DashboardActivity.DASHBOARDACTIVITY;

public class NotificationHandler extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.notification);
        Bundle bundle = getIntent().getExtras();
        String action = bundle.getString("action");
        if (action != null) {
            if (action.equals("result")) {
                //Toast.makeText(this, "Open Result Activity", Toast.LENGTH_LONG).show();
                /*if (DASHBOARDACTIVITY) {
                    startActivity(new Intent(this, DashboardActivity.class));
                    finish();
                } else {
                    //startActivity(new Intent(this, DashboardActivity.class));
                    finish();
                }*/
                Intent intent = new Intent(this, ResultsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else if (action.equals("match")) {
                Intent intent = new Intent(this, MatchViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else if (action.equals("null")){
                finish();
            }
        }



    }

}
