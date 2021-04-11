package com.example.greenbeans;



import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Client {

    String clientUID, name, email;
    ArrayList<Account> accounts = new ArrayList<>();
    ArrayList<String> accountIDList = new ArrayList();

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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
