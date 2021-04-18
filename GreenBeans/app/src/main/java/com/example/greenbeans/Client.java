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
    Double buyCost = 0.0;//cash paid for current positions
    Double portfolioValue = 0.0;//current value of all positions in all accounts
    int stage = 0;
    Double gains;//current value - cost

    public Client(String clientUID){
        this.clientUID = clientUID;
    }

    public void setClient(String name, String email, ArrayList<String> accountIDList){
        this.name = name;
        this.email = email;
        this.accountIDList = accountIDList;
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

                            if (accounts.get(accounts.size() - 1).accountCurrentVal != null) {//if account has added all positions

                                for (int y = 0; y < accounts.size(); y++) {//loop through all the clients accounts and all of their positions

                                    try {//no delay causes portfolio value to be 0... sometimes
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    portfolioValue += accounts.get(y).accountCurrentVal;
                                    buyCost += accounts.get(y).accountBuyVal;
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
    }