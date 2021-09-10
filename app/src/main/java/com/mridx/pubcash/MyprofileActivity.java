package com.mridx.pubcash;

import android.os.Bundle;
import android.view.MenuItem;

import com.mridx.pubcash.handlers.JavaStrings;
import com.mridx.pubcash.handlers.SharedPrefManager;
import com.mridx.pubcash.handlers.User;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyprofileActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private AppCompatTextView myprofileName, myprofileUsername, myprofileEmail, myprofilePhone, myprofileDob, myprofileGender, myprofileGamename, myprofileGameid;
    private CircleImageView myprofileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myprofileName = findViewById(R.id.myprofileName);
        myprofileUsername = findViewById(R.id.myprofileUsername);
        myprofileEmail = findViewById(R.id.myprofileEmail);
        myprofilePhone = findViewById(R.id.myprofilePhone);
        myprofileDob = findViewById(R.id.myprofileDob);
        myprofileGender = findViewById(R.id.myprofileGender);
        myprofileImg = findViewById(R.id.myprofileImg);
        myprofileGamename = findViewById(R.id.myprofileGamename);
        //myprofileGameid = findViewById(R.id.myprofileGameid);

        User user = SharedPrefManager.getInstance(this).getUser();
        myprofileName.setText(user.getFullname());
        myprofileGender.setText(user.getFullname());
        myprofileUsername.setText(user.getUsername());
        myprofileEmail.setText(user.getEmail());
        myprofilePhone.setText(user.getPhone());
        myprofileDob.setText(user.getDob());
        myprofileGender.setText(user.getGender());
        myprofileGamename.setText(user.getIngamename());

        Picasso.with(MyprofileActivity.this).load(JavaStrings.FIREBASE_ROOT + user.getGender() + ".png" + JavaStrings.FIREBASE_END)
                .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                .into(myprofileImg);
        //Picasso.with(MyprofileActivity.this).load(JavaStrings.FIREBASE_ROOT + user.getGender() +".png" + JavaStrings.FIREBASE_END).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(myprofileImg);

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

}
