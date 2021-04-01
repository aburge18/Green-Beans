package com.example.greenbeans;



import java.util.ArrayList;

public class Client {
    String clientUID, name, email;
    ArrayList<Account> accounts = new ArrayList<>();

    public Client(){

    }
    public Client(String name, String uID, String email){

        this.name = name;
        this.clientUID = uID;
        this.email = email;
    }



    public void addAccount(Account account){
        accounts.add(account);
    }


    public void setClient(Client client){
        this.name = client.name;
        this.clientUID = client.clientUID;
        this.email = client.email;
        this.accounts = client.accounts;
    }
}
