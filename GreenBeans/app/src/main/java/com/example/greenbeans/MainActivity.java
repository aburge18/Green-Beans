package com.example.greenbeans;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ManagerMainFragment.IListener, ManagerMainViewAdapter.IListener, ClientPortfolioFragment.IListener, AccountPositionsFragment.IListener, AccountPositionsViewAdapter.IListener, ClientPortfolioViewAdapter.IListener, BuyPositionFragment.IListener, SellPositionFragment.IListener , ConfirmBuyFragment.IListener, ConfirmSellFragment.IListener,  ClientAddAccountFragment.IListener, AddAlpacaAccountFragment.IListener{
    private FirebaseAuth mAuth;
    String userID;
    Client currentClient;
    Account currentAccount;
    Position positionToBuy;
    String symbol;
    Double quantity;

    String userType, addAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("ON MAIN");
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra("user") && getIntent().hasExtra("type")){
            mAuth = (FirebaseAuth) getIntent().getSerializableExtra("mAuth");
            userID = getIntent().getStringExtra("user");
            userType = getIntent().getStringExtra("type");
            System.out.println("USER TYPE: " + userType + userID);
        }
        if(userType.matches("manager")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new ManagerMainFragment(), "Main").addToBackStack(null).commit();
        }else if(userType.matches("client")){
            setCurrentClient();


        }
    }
    public String getUserID(){
        return userID;
    }

    @Override
    public String getAddAccount() {
        return addAccount;
    }
    public void setAddAccount(String addAccount){
        this.addAccount = addAccount;
    }


    public void setUserType(String userType){
        this.userType = userType;
    }
    public  String getUserType(){
        return userType;
    }
    public void setCurrentClient(Client client){
        this.currentClient = client;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new ClientPortfolioFragment(), "ClientPortfolio").addToBackStack(null).commit();
    }
    public void setCurrentClient(){
        currentClient = new Client(userID);
        //currentClient.setClient(userID);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new ManagerMainFragment(), "MainClient").addToBackStack(null).commit();
    }

    public Client getCurrentClient(){
/*        currentClient = new Client("3555555");
        ArrayList <String> accounts = new ArrayList<>();
        ArrayList <Account> accounts1 = new ArrayList<>();
accounts.add("3F0hbsDAB0k6BxahpZJo");
        currentClient.setClient("Noah S", "ns@taos.com", accounts);

        currentClient.accounts =  accounts1;*/
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
    @Override
    public void confirmSell(Double quantity, Position positionToBuy) {

        this.quantity = quantity;
        this.positionToBuy = positionToBuy;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new ConfirmSellFragment(), "ConfirmSell").addToBackStack(null).commit();
    }

    public String getConfirmBuyQuantity(){
        return quantity.toString();
    }
}