package com.example.greenbeans;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class AuthActivity extends AppCompatActivity implements LoginFragment.IListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, new LoginFragment(), "Login").commit();

    }

    @Override
    public void setUsername(String username) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", username);
        startActivity(intent);
    }
}