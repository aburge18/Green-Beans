package com.example.greenbeans;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;


public class ManagerMainFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    RecyclerView recView;
    LinearLayoutManager layoutManager;
    ManagerMainViewAdapter adapter;
    String managerID;
    Manager currentManager;

    int clientIndex;
    FirebaseFirestore db = FirebaseFirestore.getInstance();//initialize firestore
    public ManagerMainFragment() {
        // Required empty public constructor
    }

    public static ManagerMainFragment newInstance(String param1, String param2) {
        ManagerMainFragment fragment = new ManagerMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manager_main, container, false);

        currentManager = new Manager();
        clientIndex = 0;
        recView = view.findViewById(R.id.recView);
        managerID = mListener.getUserID();

        new getManagerAccount().execute();//gets current manager account and retrieves their client accounts

        return view;
    }


    public class getManagerAccount extends AsyncTask<String, Double, String> implements Runnable {

        @Override
        protected String doInBackground(String... strings) {
            if(mListener.getUserType().matches("manager")) {
                getManager();
            }else{
                getClient();
            }
            String str = "sda";
            return str;
        }

        @Override
        public void run() {}

        protected void onPostExecute(String client) {//when doInBackground is done executing
            super.onPostExecute(client);
        }
    }

    public void getClient(){
        currentManager = new Manager("YEET", "YEET", "YEET", "YEEY");
        Client tempClient = new Client(mListener.getUserID());
        currentManager.addClient(tempClient);
        getClients();
    }
    public void getManager(){ //Sets up current manager object

        DocumentReference managerdb = db.collection("managers").document(managerID);//get document for current manager
        managerdb.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                try {
                    JSONObject managerObj = new JSONObject(document.getData().toString());
                    JSONArray clientsArr = managerObj.getJSONArray("clients");//create array of all client UID's

                    currentManager = new Manager(managerObj.getString("fName"), managerObj.getString("lName"), managerID, managerObj.getString("email"));

                    for (int i = 0; i < clientsArr.length(); i++){
                        //Get auth tokens for each client
                        Client tempClient = new Client(clientsArr.getString(i));
                        currentManager.addClient(tempClient);

                    }

                    getClients();//get all client accounts
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getClients(){//Responsible for retrieving all Clients on manager's client list

        for (int i = 0; i < currentManager.clients.size(); i++) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("clients").document(currentManager.clients.get(i).clientUID); //get clients document
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            try {
                                JSONObject clientObj = new JSONObject(document.getData());
                                String clientName = clientObj.getString("name");
                                String clientEmail = clientObj.getString("email");
                                JSONArray accountsArr = clientObj.getJSONArray("accounts");
                                ArrayList<String> accountsList = new ArrayList<>();
                                System.out.println("CLI: " + clientName + accountsArr.length());
                                for (int i = 0; i < accountsArr.length(); i++){
                                    accountsList.add(accountsArr.get(i).toString());
                                }
                                currentManager.clients.get(clientIndex).setClient(clientName, clientEmail, accountsList);
                                System.out.println(currentManager.clients.get(0).name);
                                currentManager.clients.get(clientIndex).getGainsStart();
                                //currentManager.clients.get(clientIndex).getAccounts();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            clientIndex++;
                            adapter.notifyDataSetChanged();
                        } else {
                            System.out.println("No such client document");
                        }
                    } else {
                        System.out.println("get failed with " + task.getException());
                    }
                }
            });
        }
        adapter = new ManagerMainViewAdapter(currentManager.clients, getContext());
        layoutManager = new LinearLayoutManager(getContext());
        recView.setLayoutManager(layoutManager);
        recView.setAdapter(adapter);

    }



    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if (context instanceof IListener){
            mListener = (IListener)context;
        }else{
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }
    IListener mListener;

    public interface IListener{
        String getUserID();
        String getUserType();
    }
}