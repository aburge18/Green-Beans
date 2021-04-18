package com.example.greenbeans.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenbeans.ClientAddAccountFragment;
import com.example.greenbeans.MainActivity;
import com.example.greenbeans.R;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity implements LoginFragment.IListener, CreateAccountFragment.IListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, new LoginFragment(), "Login").commit();

    }
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    public void setUsername(String userID) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", userID);
        startActivity(intent);
    }
}