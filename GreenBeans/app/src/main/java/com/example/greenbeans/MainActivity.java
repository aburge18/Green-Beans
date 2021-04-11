package com.example.greenbeans;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ManagerMainFragment.IListener, ManagerMainViewAdapter.IListener, ClientPortfolioFragment.IListener, AccountPositionsFragment.IListener, AccountPositionsViewAdapter.IListener, ClientPortfolioViewAdapter.IListener, BuyPositionFragment.IListener, ConfirmBuyFragment.IListener{
    private FirebaseAuth mAuth;
    String userID;
    Client currentClient;
    Account currentAccount;
    Position positionToBuy;
    String symbol;
    Double quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra("user")){
            mAuth = (FirebaseAuth) getIntent().getSerializableExtra("mAuth");
            userID = getIntent().getStringExtra("user");
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new ManagerMainFragment(), "Main").addToBackStack(null).commit();
    }
    public String getUserID(){
        return userID;
    }

    public void setCurrentClient(Client client){
        this.currentClient = client;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new ClientPortfolioFragment(), "ClientPortfolio").addToBackStack(null).commit();
    }

    public Client getCurrentClient(){
        return currentClient;
    }

    @Override
    public void setCurrentAccount(Account account) {
        this.currentAccount = account;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new AccountPositionsFragment(), "AccountPositions").addToBackStack(null).commit();
    }

    public Account getCurrentAccount(){
        return currentAccount;
    }

    public void setPositionToBuy(){//open buy fragment to buy any position
        positionToBuy = null;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new BuyPositionFragment(), "BuyPositions").addToBackStack(null).commit();
    }
    public void setPositionToBuy(Position positionToBuy){//open buy fragment to specified any position
        this.positionToBuy = positionToBuy;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new BuyPositionFragment(), "BuyPositions").addToBackStack(null).commit();
    }
    public void setPositionToSell(Position positionToBuy){
        this.positionToBuy = positionToBuy;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new SellPositionFragment(), "SellPositions").addToBackStack(null).commit();
    }
    public Position getPositionToBuy(){
        return positionToBuy;
    }

    @Override
    public void confirmBuy(Double quantity, Position positionToBuy) {

        this.quantity = quantity;
        this.positionToBuy = positionToBuy;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new ConfirmBuyFragment(), "ConfirmBuy").addToBackStack(null).commit();
    }

    public String getConfirmBuyQuantity(){
        return quantity.toString();
    }
}