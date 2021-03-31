package com.example.greenbeans;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements ManagerMainFragment.IListener{
    private FirebaseAuth mAuth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra("user")){
            mAuth = (FirebaseAuth) getIntent().getSerializableExtra("mAuth");
            userID = getIntent().getStringExtra("user");
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new ManagerMainFragment(), "Main").commit();
    }
    public String getUserID(){
        return userID;
    }
}