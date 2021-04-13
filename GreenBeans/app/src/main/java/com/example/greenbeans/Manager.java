package com.example.greenbeans;

import java.util.ArrayList;

public class Manager {

    String fName, lName, uID, email;
    ArrayList<Client> clients = new ArrayList<>();

    public Manager (String fName, String lName, String uID, String email){
        this.fName = fName;
        this.lName = lName;
        this.uID = uID;
        this.email = email;
    }
    public Manager(){}

    public void addClient(Client client){
        clients.add(client);
    }
    public void setClients(ArrayList<Client> clients){
        this.clients = clients;
    }
}
