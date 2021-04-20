package com.example.greenbeans.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenbeans.Account;
import com.example.greenbeans.AssignManagerFragment;
import com.example.greenbeans.ClientAddAccountFragment;
import com.example.greenbeans.MainActivity;
import com.example.greenbeans.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity implements LoginFragment.IListener, CreateAccountFragment.IListener {
    String createType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, new LoginFragment(), "Login").commit();
    }

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    public void setUsername(String userID) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();//initialize firestore
        DocumentReference clientAccount = db.collection("clients").document(userID);//check if user is a client
        clientAccount.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                //JSONObject clientInfo = new JSONObject(document.getData());
                if(task.getResult().getData() == null) {//if person signing in is not a client
                    System.out.println("Info: " +  task.getResult().getData());
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference managerAccount = db.collection("managers").document(userID);//check if they are a manager
                    managerAccount.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            System.out.println("Info2: " +  task.getResult().getData() + "and" + document.toString());
                            if(task.getResult().getData() != null){
                                loginManager(userID);
                            }
                        }
                    });
                }else{//if user if a client
                    loginClient(userID);
                }
            }
        });

    }

    public void setCreateType(String createType){
        this.createType = createType;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new CreateAccountFragment(), "New").addToBackStack(null).commit();
    }

    public String getCreateType(){
        return createType;
    }

    public void loginClient(String userID){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", userID);
        intent.putExtra("type", "client");
        startActivity(intent);
    }
    public void loginManager(String userID){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", userID);
        intent.putExtra("type", "manager");
        startActivity(intent);
    }
}