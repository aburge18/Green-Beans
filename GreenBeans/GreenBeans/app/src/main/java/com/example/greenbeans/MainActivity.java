package com.example.greenbeans;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements ManagerMainFragment.IListener, ManagerMainViewAdapter.IListener, ClientPortfolioFragment.IListener, AccountPositionsFragment.IListener, ClientPortfolioViewAdapter.IListener{
    private FirebaseAuth mAuth;
    String userID;

    Client currentClient;
    Account currentAccount;
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

    public void setCurrentClient(Client client){
        this.currentClient = client;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new ClientPortfolioFragment(), "ClientPortfolio").commit();
    }
    public Client getCurrentClient(){
        return currentClient;
    }

    @Override
    public void setCurrentAccount(Account account) {

        this.currentAccount = account;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new AccountPositionsFragment(), "AccountPositions").commit();
    }

    public Account getCurrentAccount(){
        return currentAccount;
    }
}