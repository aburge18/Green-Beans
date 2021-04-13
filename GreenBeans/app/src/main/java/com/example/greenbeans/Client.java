package com.example.greenbeans;



import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Client {

    String clientUID, name, email, gainsStr;
    ArrayList<Account> accounts = new ArrayList<>();
    ArrayList<String> accountIDList = new ArrayList();
    Double buyCost = 0.0;
    Double portfolioValue = 0.0;
    int stage = 0;
    Double gains;
    public Client(){}

    public Client(String clientUID){
        this.clientUID = clientUID;
    }

    public Client(String name, String uID, String email){
        this.name = name;
        this.clientUID = uID;
        this.email = email;
    }

    public void addAccount(Account account){
        accounts.add(account);
    }

    public void setClient(String name, String email, ArrayList<String> accountIDList){
        this.name = name;
        this.email = email;
        this.accountIDList = accountIDList;
    }

    public void getAccounts(){//get accountID's from firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();//initialize firestore
        Account tempAccount = new Account();

        for (int i = 0; i < accountIDList.size(); i++) {
            DocumentReference accountInfo = db.collection("accounts").document(accountIDList.get(i));
            accountInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    DocumentSnapshot document = task.getResult();
                    JSONObject accountInfo = new JSONObject(document.getData());

                    try {
                        tempAccount.addAccount(accountInfo.getString("accountType"), accountInfo.getString("refreshToken"), accountInfo.getString("lastRefresh"));
                        accounts.add(tempAccount);
                        if (accountIDList.size() == accounts.size()){
                            stage = 2;
                            System.out.println("STAGE 1:");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

    }

    public void getGainsStart(){
        if (stage == 0) {
            System.out.println("Running: " + name);
            stage = 1;
            GetGainsRunnable runnable = new GetGainsRunnable();
            new Thread(runnable).start();
        }
    }

    class GetGainsRunnable implements Runnable{

        @Override
        public void run() {
            getAccounts();
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < accounts.size(); i++){
                accounts.get(i).setAuthTokenViaRefresh();
            }

            while (stage != 4) {//while getting all positions from all accounts
                if(accounts.get(accounts.size() - 1).stage == 2){//if last account has its refreshtoken
                    if (stage == 2) {
                        stage = 3;//set next stage
                            for (int z = 0; z < accounts.size(); z++) {//get all positions in each account
                                accounts.get(z).addPositions();
                            }
                            if (accounts.get(accounts.size() - 1).stage == 4) {//if account has added all positions

                                for (int y = 0; y < accounts.size(); y++) {//loop through all the clients accounts and all of their positions
                                    for (int x = 0; x < accounts.get(y).positions.size(); x++) {
                                        //curent value of all positions
                                        portfolioValue += (Double.valueOf(accounts.get(y).positions.get(x).quantity) * accounts.get(y).positions.get(x).currentPriceVal);

                                        //value of all positions when purchased
                                        buyCost += (accounts.get(y).positions.get(x).buyPriceNum * Double.valueOf(accounts.get(y).positions.get(x).quantity));

                                        //System.out.println("BUY FOR: " + name + " " + accounts.get(y).positions.get(x).buyPriceNum + "-" + Double.valueOf(accounts.get(y).positions.get(x).quantity));
                                        //System.out.println( x + " " + y + " :XY" + buyCost + " + " + portfolioValue);
                                        //System.out.println("SIZE... " + accounts.size());
                                    }
                                }
                                //set stage to 4 to end while loop
                                stage = 4;
                                DecimalFormat df = new DecimalFormat("0.00");
                                gains = portfolioValue - buyCost;
                                gainsStr = df.format(gains);//get clients gains for all accounts
                            }

                        }
                    }
                }
            }
        }
    }
