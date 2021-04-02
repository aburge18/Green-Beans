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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManagerMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManagerMainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String authCode;
    // TODO: Rename and change types of parameters
    private final okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
    ManagerMainViewAdapter adapter;
    ArrayList<Client> listOfClients = new ArrayList<>();
    private String mParam1;
    private String mParam2;
    RecyclerView recView;
    LinearLayoutManager layoutManager;
    ArrayList tokens = new ArrayList();
    String symbol;
    TextView tv;
    String managerID;
    Manager currentManager;

    FirebaseFirestore db = FirebaseFirestore.getInstance();//initialize firestore
    public ManagerMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManagerMainFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        recView = view.findViewById(R.id.recView);
        managerID = mListener.getUserID();


        new getClientsAccounts().execute();

        return view;

    }

    public void getManager(){ //Sets up current manager object

        DocumentReference managerdb = db.collection("managers").document(managerID);//get document for current manager
        managerdb.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                System.out.println("MANAGERS: " + document.getData().toString());
                try {
                    JSONObject managerObj = new JSONObject(document.getData().toString());
                    JSONArray clientsArr = managerObj.getJSONArray("clients");//create array of all client UID's

                    currentManager = new Manager(managerObj.getString("fName"), managerObj.getString("lName"), managerID, managerObj.getString("email"));


                    for (int i = 0; i < clientsArr.length(); i++){
                        //Get auth tokens for each client

                        currentManager.addClient(getClients(clientsArr.getString(i)));
                        System.out.println("Size 3: " + currentManager.clients.size());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public Client getClients(String clientID){//Responsible for retrieving all Clients on manager's client list

        Client client = new Client();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("clients").document(clientID); //get clients document
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        System.out.println("DocumentSnapshot data: " + document.getData().toString());

                        try {
                            JSONObject tokensObj = new JSONObject(document.getData());

                            String refreshToken = tokensObj.getString("refreshtoken");
                            String clientName = tokensObj.getString("name");
                            String clientEmail = tokensObj.getString("email");
                            JSONArray accounts = tokensObj.getJSONArray("accounts");
                            System.out.println("ACCOUNTS: " + accounts.getString(0));
                            Client tempClient = new Client(clientName, clientID, clientEmail);
                            System.out.println("Size 4: " + currentManager.clients.size());

                            currentManager.clients.get(currentManager.clients.size() - 1).setClient(tempClient);


                            System.out.println("Size 5: " + currentManager.clients.size());
                            for (int i = 0; i < accounts.length(); i++) {

                                Account tempAccount = getAccount(accounts.getString(0), tempClient);
                                tempClient.addAccount(tempAccount);
                                System.out.println("SUPER TEMP: " + tempAccount.refreshToken);
                                System.out.println("Size 1: " + currentManager.clients.size());





                                System.out.println("Size 2: " + currentManager.clients.size());
                                client.setClient(tempClient);
                                System.out.println("CLIENTSS: " + client.accounts.get(0).lastRefresh + " : " + clientID + " : " + tempAccount.refreshToken);
                            }
                           tokens.add(refreshToken);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        System.out.println("No such document");
                    }
                } else {
                    System.out.println("get failed with " + task.getException());
                }
            }
        });

        return client;
    }
    public Account getAccount(String accountID, Client client) throws JSONException {


        Account tempAccount = new Account();


        DocumentReference accountInfo = db.collection("accounts").document(accountID);

        accountInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override

                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                    JSONObject accountInfo = new JSONObject(document.getData());

                    try {
                        System.out.println("ACCOUNT2: " + accountInfo.getString("refreshToken"));tempAccount.addAccount(accountInfo.getString("accountType"), accountInfo.getString("refreshToken"), accountInfo.getString("lastRefresh"));

                        System.out.println("FInal test: " + currentManager.lName + currentManager.clients.get(0).email + currentManager.clients.get(0).clientUID);
                        new setAuth().execute();

                        System.out.println("TEMP: " + tempAccount.refreshToken);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        return tempAccount;
    }


    public class setAuth extends AsyncTask<String, Double, String> implements Runnable {

        @Override
        protected String doInBackground(String... strings) {
            for (int i = 0; i < currentManager.clients.size(); i++){
                for (int x = 0; x < currentManager.clients.get(i).accounts.size(); x++){
                    currentManager.clients.get(i).accounts.get(x).setAuthTokenViaRefresh();
                    System.out.println(currentManager.clients.size() + "i = " + i + " : x = " + x);
                }
                listOfClients.add(currentManager.clients.get(i));
            }
            return "ssf";
        }

        @Override
        public void run() {

        }
        protected void onPostExecute(String client) {//when doInBackground is done executing
            super.onPostExecute(client);

            adapter = new ManagerMainViewAdapter(listOfClients, getContext());
            layoutManager = new LinearLayoutManager(getContext());
            recView.setLayoutManager(layoutManager);
            recView.setAdapter(adapter);

        }
    }


    public class getClientsAccounts extends AsyncTask<String, Double, String> implements Runnable {

        @Override
        protected String doInBackground(String... strings) {
            getManager();
            String str = "sda";
            return str;
        }

        @Override
        public void run() {

        }
        protected void onPostExecute(String client) {//when doInBackground is done executing
            super.onPostExecute(client);

        }
    }




    public String getAuthTokenViaRefresh(String token){

        FormBody formBody = new FormBody.Builder().add("grant_type", "refresh_token").add("refresh_token", token).add("client_id", "HJ8DN850FB0BCX4ZCYCZK85SDKLKPLX7").add("redirect_uri", "http://localhost").build();

        final Request request = new Request.Builder().url("https://api.tdameritrade.com/v1/oauth2/token").post(formBody).addHeader("Content-Type", "application/x-www-form-urlencoded").build();


        String response1Body;
        try {
            try(Response response1 = client.newCall(request).execute()) {
                if (!response1.isSuccessful()) try {
                    throw new IOException("Unexpected code " + response1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                response1Body = response1.body().string();
                JSONObject responseObj = new JSONObject(response1Body);
                String access = responseObj.getString("access_token");



                authCode = access;

                authCode = "Bearer " + authCode;

                System.out.println("Auth Code: " + authCode);
                new Positions().execute();
                System.out.println(response1Body);
                System.out.println("Response 1 response:          " + response1);
                System.out.println("Response 1 cache response:    " + response1.cacheResponse());
                System.out.println("Response 1 network response:  " + response1.networkResponse());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return authCode;
    }


    public class Positions extends AsyncTask<Integer, Double, ArrayList<Double>> implements Runnable {


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public ArrayList<Double> doInBackground(Integer... values) {
            ArrayList<Double> avgs = new ArrayList<Double>();//temp list to send to postExecute

            final Request request = new Request.Builder().url("https://api.tdameritrade.com/v1/accounts?fields=positions").addHeader("Authorization", authCode).build();

            String response1Body;
            try {
                try(Response response1 = client.newCall(request).execute()) {
                    if (!response1.isSuccessful()) try {
                        throw new IOException("Unexpected code " + response1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    response1Body = response1.body().string();
                    System.out.println("Response 2: " + response1Body);
                    JSONArray responseArray = new JSONArray(response1Body);

                    JSONObject responseObj = responseArray.getJSONObject(0);
                    JSONObject securitiesAccountObj =  responseObj.getJSONObject("securitiesAccount");
                    JSONArray positionsArr = securitiesAccountObj.getJSONArray("positions");
                    JSONObject positionObj = positionsArr.getJSONObject(0);
                    JSONObject instrumentObj= positionObj.getJSONObject("instrument");
                    symbol = instrumentObj.getString("symbol");
                    System.out.println("Symbol: " + symbol);

                    String numOfPos = positionObj.getString("longQuantity");
                    numOfPos = numOfPos.substring(0, numOfPos.length() - 2);
                    int posNum = Integer.valueOf(numOfPos);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                        }
                    });
                    System.out.println("Response 1 response:          " + response1);
                    System.out.println("Response 1 cache response:    " + response1.cacheResponse());
                    System.out.println("Response 1 network response:  " + response1.networkResponse());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return avgs;//send list of nums to postExecute
        }

        protected void onProgressUpdate(Double... values) {

        }

        protected void onPostExecute(ArrayList<Double> avg) {//when doInBackground is done executing
            super.onPostExecute(avg);


        }

        @Override
        public void run() {



        }
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
    }
}